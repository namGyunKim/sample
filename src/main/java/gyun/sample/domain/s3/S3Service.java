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

    public String uploadFileWithDisposition(UploadDirect uploadDirect, MultipartFile file, long memberId) throws IOException {

        validation(file);

        long entityId = getEntityId(memberId, uploadDirect);

        // 현재 날짜로 파일 이름 생성
        String newFilename = uploadDirect.getValue() + "_" + entityId + getFileExtension(file.getOriginalFilename());

        final String key = generatedKey(newFilename, uploadDirect); // 폴더명 + 새로운 파일명 설정

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentDisposition("attachment; filename=\"" + newFilename + "\"")
                .build();

        // RequestBody.fromInputStream()를 사용하여 MultipartFile의 InputStream을 전달
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//        이미지를 처리하는 로직
        processImage(entityId, newFilename, uploadDirect);

        // key 값을 반환하여 호출자가 이 key로 파일을 식별할 수 있도록 함
        return key;
    }

    public void deleteFile(String fileName, UploadDirect uploadDirect) {
        final String key = generatedKey(fileName, uploadDirect);

        if (!doesObjectExist(key)) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND);
        }


        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }


    public String getFileUrl(String fileName, UploadDirect uploadDirect) {
        final String key = generatedKey(fileName, uploadDirect);


        if (!doesObjectExist(key)) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    private String generatedKey(String fileName, UploadDirect uploadDirect) {
        return uploadDirect.getValue() + "/" + fileName;
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

    public void processImage(long entityId, String fileName, UploadDirect uploadDirect) {
        if (uploadDirect == UploadDirect.MEMBER_PROFILE) {
            updateProfileImage(entityId, fileName, uploadDirect);
        }
    }

    public void updateProfileImage(long memberId, String fileName, UploadDirect uploadDirect) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        }

        member.updateProfileImage(fileName, uploadDirect);
    }

    private void validation(MultipartFile file) {
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
            return memberId;
        }
        return 0;
    }

}
