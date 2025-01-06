package gyun.sample.domain.s3.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.MemberImage;
import gyun.sample.domain.member.repository.MemberImageRepository;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.UtilService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class S3MemberService implements S3Service {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucketName;
    private final UtilService utilService;


    // 허용된 확장자 리스트
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpeg", ".jpg", ".png", ".gif", ".heif", ".heic", ".bmp", ".webp", ".tiff");
    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

    @Value("${s3.region}")
    private String region;
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;

    private final UploadDirect uploadDirect = UploadDirect.MEMBER_PROFILE;
    @Value("${s3.bucket-local}")
    private String localBucketName;

    @PostConstruct
    public void init() {
        boolean localProfile = utilService.isLocalProfile();
        if (localProfile) {
            bucketName = localBucketName;
        }
    }

    //    확장자 추출
    public static String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return "." + filename.substring(dotIndex + 1).toLowerCase();
        }

        return "";
    }

    @Override
    public List<String> upload(List<MultipartFile> files, long memberId) {
        if (files == null || files.isEmpty()) return List.of();

        if (!StringUtils.hasText(files.get(0).getOriginalFilename())) return List.of();

        // 결과를 리스트로 변환
        return files.stream()
                .map(file -> upload(file, memberId)) // 각 파일 업로드 실행
                .toList(); // 업로드된 파일 키 리스트 반환
    }

    @Override
    public void deleteFile(List<String> fileNames) {

        if (fileNames == null || fileNames.isEmpty()) return;

        if (!StringUtils.hasText(fileNames.get(0))) return;

        fileNames.forEach(this::deleteFile);
    }

    @Override
    public String getFileUrl(String fileName) {
        final String key = generatedKey(fileName);


        if (!doesObjectExist(key)) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    //    업로드할때 파일명을 생성하는 메서드
    private String generatedKeyWithUpload(String originalFilename) {
        final String UUID = java.util.UUID.randomUUID().toString();
        return uploadDirect.getValue() + "/" + UUID + "_" + originalFilename;
    }


    private boolean doesObjectExist(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    private void validationFile(MultipartFile file) {
        // 확장자 체크
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new GlobalException(ErrorCode.FILE_FORMAT_INVALID);
        }

        // 파일 크기 체크
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GlobalException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    private String generatedKey(String fileName) {
        return uploadDirect.getValue() + "/" + fileName;
    }

    public Member getEntityId(long memberId) {
        return memberRepository.findByIdAndActive(memberId, GlobalActiveEnums.ACTIVE)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
    }

    public void saveExtension(Member member, String fileName) {
        MemberImage memberImage = new MemberImage(uploadDirect, fileName, member);
        member.addImage(memberImage);
    }

    public String upload(MultipartFile file, long memberId) {
        // 파일 검증 (확장자, 용량)
        validationFile(file);

        // 엔티티 유효성 확인 후 엔티티 ID 가져오기
        Member member = getEntityId(memberId);

        final String originalFilename = file.getOriginalFilename();

        // S3 업로드 키 생성
        final String key = generatedKeyWithUpload(originalFilename);
        final String returnKey = key.substring(key.indexOf("/") + 1);

        final String downloadFileName = utilService.encodeFileName(originalFilename);

        // S3에 업로드할 요청 객체 생성 (Content-Disposition 설정 포함)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentDisposition("attachment; filename=\"" + downloadFileName + "\"")
                .build();

        // S3 업로드 시도
        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (Exception e) {
            // S3 업로드 실패 시 예외 처리
            // 필요하다면 로그나 특정 에러 코드로 변환 가능
            throw new GlobalException(ErrorCode.UPLOAD_FAILED, e);
        }

        // 업로드 성공 시 DB에 확장자 정보 업데이트
        saveExtension(member, returnKey);

        // 업로드 성공 후 key 반환
        return returnKey;
    }

    public void deleteFile(String fileName) {
        final String key = generatedKey(fileName);

        if (!doesObjectExist(key)) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND);
        }

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        MemberImage memberImage = memberImageRepository.findByFileNameAndUploadDirect(fileName, uploadDirect)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_IMAGE_NOT_EXIST));

        memberImageRepository.delete(memberImage);


    }

}
