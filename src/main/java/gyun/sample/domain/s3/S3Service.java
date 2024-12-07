package gyun.sample.domain.s3;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucketName;


    // 허용된 확장자 리스트
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpeg", ".jpg", ".png", ".gif", ".heif", ".heic", ".bmp", ".webp", ".tiff");
    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

    @Value("${s3.region}")
    private String region;
    private final MemberRepository memberRepository;
    @Value("${s3.bucket-local}")
    private String localBucketName;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    //    확장자 추출
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return "." + filename.substring(dotIndex + 1).toLowerCase();
        }

        return "";
    }

    @PostConstruct
    public void init() {
        if (!"prod".equals(activeProfile)) {
            bucketName = localBucketName;
        }
    }

    //    샘플이라  로그인한 계정 프로필로 가정
    public String uploadFileWithDisposition(UploadDirect uploadDirect, MultipartFile file, long memberId) throws IOException {
        // 파일 검증 (확장자, 용량)
        validationFile(file);

        // 엔티티 유효성 확인 후 엔티티 ID 가져오기
        long entityId = getEntityId(memberId, uploadDirect);

        // 파일 확장자 추출
        String fileExtension = getFileExtension(file.getOriginalFilename());

        // S3 업로드 키 생성
        final String key = generatedKeyWithUpload(entityId, uploadDirect, fileExtension);

        // S3에 업로드할 요청 객체 생성 (Content-Disposition 설정 포함)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentDisposition("attachment; filename=\"" + entityId + fileExtension + "\"")
                .build();

        // 기존 파일 삭제 (존재하지 않아도 예외 없이 통과)
        deleteFile(entityId, uploadDirect);

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
        saveExtension(entityId, uploadDirect, fileExtension);

        // 업로드 성공 후 key 반환
        return key;
    }


    public void deleteFile(long entityId, UploadDirect uploadDirect) {
        final String key = generatedKey(entityId, uploadDirect);


        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }


    public String getFileUrl(long entityId, UploadDirect uploadDirect) {
        final String key = generatedKey(entityId, uploadDirect);


        if (!doesObjectExist(key)) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    //    업로드할때 파일명을 생성하는 메서드
    private String generatedKeyWithUpload(long entityId, UploadDirect uploadDirect, String fileExtension) {
        return uploadDirect.getValue() + "/" + entityId + fileExtension;
    }

    //    업로드가 아닌 경우 파일명을 생성하는 메서드
// 업로드가 아닌 경우 파일명을 생성하는 메서드
    public String generatedKey(long entityId, UploadDirect uploadDirect) {
        String extension = switch (uploadDirect) {
            case MEMBER_PROFILE -> memberRepository.findByIdAndActive(entityId, GlobalActiveEnums.ACTIVE)
                    .map(Member::getImageExtension)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_ACTIVE_MEMBER));
            default -> "";
        };

        // 여기서 generatedKey를 호출해야 합니다.
        return generatedKeyWithUpload(entityId, uploadDirect, extension);
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
            throw new GlobalException(ErrorCode.INVALID_FILE_FORMAT);
        }

        // 파일 크기 체크
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GlobalException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    public long getEntityId(long memberId, UploadDirect uploadDirect) {
        if (uploadDirect == UploadDirect.MEMBER_PROFILE) {
            return memberRepository.findById(memberId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER)).getId();
        } else throw new GlobalException(ErrorCode.NOT_EXIST_ENTITY);
    }

    public void saveExtension(long entityId, UploadDirect uploadDirect, String extension) {
        // 확장자 저장
        if (uploadDirect == UploadDirect.MEMBER_PROFILE) {
            Member member = memberRepository.findByIdAndActive(entityId, GlobalActiveEnums.ACTIVE)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_ACTIVE_MEMBER));
            member.updateProfileExtension(extension);
        }
    }


}
