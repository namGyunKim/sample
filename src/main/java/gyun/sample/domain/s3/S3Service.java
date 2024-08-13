package gyun.sample.domain.s3;

import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucketName;

    @Value("${s3.region}")
    private String region;

    public String uploadFileWithDisposition(UploadDirect uploadDirect, MultipartFile file) throws IOException {
        // UUID와 현재 날짜로 파일 이름 생성
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalFilename = file.getOriginalFilename();
        String newFilename = timestamp + "_" + originalFilename;

        final String key = generatedKey(newFilename, uploadDirect); // 폴더명 + 새로운 파일명 설정

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentDisposition("attachment; filename=\"" + newFilename + "\"")
                .build();

        // RequestBody.fromInputStream()를 사용하여 MultipartFile의 InputStream을 전달
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

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

}
