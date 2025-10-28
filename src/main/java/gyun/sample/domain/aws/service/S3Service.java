package gyun.sample.domain.aws.service;

import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.aws.payload.dto.ImageUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {

    ImageUploadResult uploadImage(MultipartFile file, ImageType imageType, Long entityId);

    List<ImageUploadResult> uploadImages(List<MultipartFile> files, ImageType imageType, Long entityId);

    ImageUploadResult uploadImageFromUrl(String imageUrl, ImageType imageType, Long entityId);

    List<ImageUploadResult> uploadImagesFromUrls(List<String> imageUrls, ImageType imageType, Long entityId);

    void deleteImage(String fileName, ImageType imageType, Long entityId);

    void deleteImages(List<String> fileNames, ImageType imageType, Long entityId);

    String getImageUrl(String fileName, ImageType imageType, Long entityId);


    /**
     * S3 내의 한 객체(이미지)를 다른 경로로 복사합니다. (S3-to-S3 copy)
     *
     * @param sourceS3Url          원본 S3 객체 URL (getImageUrl로 생성된 URL)
     * @param destinationImageType 복사될 대상의 이미지 타입
     * @param destinationEntityId  복사될 대상의 엔티티 ID
     * @return 복사된 이미지의 업로드 결과 (S3-to-S3 복사시 크기 정보는 null)
     */
    ImageUploadResult cloneImageFromUrl(String sourceS3Url, ImageType destinationImageType, Long destinationEntityId);

    /**
     * S3 내의 여러 객체(이미지)를 다른 경로로 복사합니다.
     *
     * @param sourceS3Urls         원본 S3 객체 URL 목록
     * @param destinationImageType 복사될 대상의 이미지 타입
     * @param destinationEntityId  복사될 대상의 엔티티 ID
     * @return 복사된 이미지의 업로드 결과 목록 (S3-to-S3 복사시 크기 정보는 null)
     */
    List<ImageUploadResult> cloneImagesFromUrls(List<String> sourceS3Urls, ImageType destinationImageType, Long destinationEntityId);
}

