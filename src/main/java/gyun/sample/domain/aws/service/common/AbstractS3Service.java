package gyun.sample.domain.aws.service.common;

import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.aws.payload.dto.ImageUploadResult;
import gyun.sample.domain.aws.payload.dto.S3UrlParts;
import gyun.sample.domain.aws.service.S3Service;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * S3Service의 공통 로직을 구현하는 추상 클래스.
 * S3에 접근하는 구현체(Service)는 이 클래스를 상속받아 사용합니다.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractS3Service implements S3Service {

    protected final S3Client s3Client;

    @Value("${s3.bucket}")
    protected String bucketName;
    @Value("${s3.region}")
    protected String region;

    /**
     * S3 구현체(Banner, Profile 등)에 특화된 이미지 타입 유효성 검사를 수행합니다.
     *
     * @param imageType 검사할 이미지 타입
     */
    protected abstract void validateImageType(ImageType imageType);

    @Override
    public ImageUploadResult uploadImage(MultipartFile file, ImageType imageType, Long entityId) {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        try {
            // 구체적인 유효성 검사 로직을 하위 클래스에 위임
            validateImageType(imageType);
            return uploadToS3Internal(file.getBytes(), originalFilename, imageType, entityId);
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패 (파일 바이트 변환 오류): {}, entityId: {}", originalFilename, entityId, e);
            throw new GlobalException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public List<ImageUploadResult> uploadImages(List<MultipartFile> files, ImageType imageType, Long entityId) {
        return files.stream()
                .map(file -> uploadImage(file, imageType, entityId))
                .collect(Collectors.toList());
    }

    @Override
    public ImageUploadResult uploadImageFromUrl(String imageUrl, ImageType imageType, Long entityId) {
        try {
            log.info("이미지 다운로드 시도. URL: {}", imageUrl);
            // 구체적인 유효성 검사 로직을 하위 클래스에 위임
            validateImageType(imageType);
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("URL에서 이미지 다운로드 실패. URL: {}, 응답 코드: {}", imageUrl, connection.getResponseCode());
                throw new GlobalException(ErrorCode.FILE_DOWNLOAD_FAILED);
            }

            byte[] fileBytes;
            try (InputStream inputStream = connection.getInputStream()) {
                fileBytes = inputStream.readAllBytes();
            }

            String originalFilename = extractOriginalFilenameFromUrl(imageUrl);

            return uploadToS3Internal(fileBytes, originalFilename, imageType, entityId);

        } catch (IOException e) {
            log.error("URL에서 이미지를 읽는 중 오류 발생: {}", imageUrl, e);
            throw new GlobalException(ErrorCode.FILE_DOWNLOAD_FAILED, "URL에서 이미지 다운로드 실패: " + imageUrl);
        }
    }

    @Override
    public List<ImageUploadResult> uploadImagesFromUrls(List<String> imageUrls, ImageType imageType, Long entityId) {
        return imageUrls.stream()
                .map(url -> uploadImageFromUrl(url, imageType, entityId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(String fileName, ImageType imageType, Long entityId) {
        String s3Key = generateS3Key(fileName, imageType, entityId);
        if (!doesObjectExist(s3Key)) {
            return;
        }
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void deleteImages(List<String> fileNames, ImageType imageType, Long entityId) {
        fileNames.forEach(fileName -> deleteImage(fileName, imageType, entityId));
    }

    @Override
    public String getImageUrl(String fileName, ImageType imageType, Long entityId) {
        String singleEncodedS3Key = generateS3Key(fileName, imageType, entityId);

        if (!doesObjectExist(singleEncodedS3Key)) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND, "파일이 S3에 존재하지 않습니다: " + singleEncodedS3Key);
        }
        String doubleEncodedPath = singleEncodedS3Key.replace("%", "%25");

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, doubleEncodedPath);
    }

    @Override
    public ImageUploadResult cloneImageFromUrl(String sourceS3Url, ImageType destinationImageType, Long destinationEntityId) {
        log.info("S3-to-S3 복사 시작. Source URL: {}, DestType: {}, DestEntityId: {}",
                sourceS3Url, destinationImageType.name(), destinationEntityId);

        // 구체적인 유효성 검사 로직을 하위 클래스에 위임
        validateImageType(destinationImageType);

        S3UrlParts source = parseS3Url(sourceS3Url);

        HeadObjectResponse headResponse;
        String originalFilename;
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(source.getBucketName())
                    .key(source.getObjectKey())
                    .build();
            headResponse = s3Client.headObject(headRequest);
            originalFilename = extractFilenameFromContentDisposition(headResponse.contentDisposition());
            log.info("원본 파일명 추출 성공: {}", originalFilename);
        } catch (NoSuchKeyException e) {
            log.error("S3 복사 실패. 원본 파일을 찾을 수 없습니다: {}", source.getObjectKey());
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND, "Source S3 file not found: " + source.getObjectKey());
        } catch (Exception e) {
            log.error("S3 원본 파일 메타데이터 조회 실패: {}", source.getObjectKey(), e);
            throw new GlobalException(ErrorCode.FILE_DOWNLOAD_FAILED, "Failed to read source S3 metadata.");
        }

        String finalFileName = generateFinalUploadFileName(destinationImageType, originalFilename);
        String destinationKey = generateS3Key(finalFileName, destinationImageType, destinationEntityId);

        if (doesObjectExist(destinationKey)) {
            log.warn("S3에 동일한 파일이 이미 존재하여 복사를 건너뜁니다. Key: {}", destinationKey);
            return createImageUploadResult(finalFileName, null, null);
        }

        try {
            String encodedOriginalFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8).replace("+", "%20");
            String newContentDisposition = "attachment; filename*=\"UTF-8''" + encodedOriginalFilename + "\"";

            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(source.getBucketName())
                    .sourceKey(source.getObjectKey())
                    .destinationBucket(bucketName)
                    .destinationKey(destinationKey)
                    .contentDisposition(newContentDisposition)
                    .metadataDirective(MetadataDirective.REPLACE)
                    .build();

            s3Client.copyObject(copyRequest);
            log.info("S3-to-S3 복사 성공. DestKey: {}", destinationKey);

        } catch (Exception e) {
            log.error("S3-to-S3 복사 실패. Source: {}, Dest: {}", source.getObjectKey(), destinationKey, e);
            throw new GlobalException(ErrorCode.FILE_UPLOAD_FAILED, "S3-to-S3 copy failed.");
        }

        return createImageUploadResult(finalFileName, null, null);
    }

    @Override
    public List<ImageUploadResult> cloneImagesFromUrls(List<String> sourceS3Urls, ImageType destinationImageType, Long destinationEntityId) {
        return sourceS3Urls.stream()
                .map(url -> cloneImageFromUrl(url, destinationImageType, destinationEntityId))
                .collect(Collectors.toList());
    }

    // === 공통 헬퍼 메서드 (Protected) ===

    protected ImageUploadResult uploadToS3Internal(byte[] fileBytes, String originalFilename, ImageType imageType, Long entityId) {
        validateFile(fileBytes, originalFilename, imageType);

        String finalFileName = generateFinalUploadFileName(imageType, originalFilename);
        String s3Key = generateS3Key(finalFileName, imageType, entityId);

        Integer width = null;
        Integer height = null;
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            BufferedImage image = ImageIO.read(is);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            } else {
                log.warn("이미지 데이터를 읽을 수 없어 크기를 측정할 수 없습니다: {}", originalFilename);
            }
        } catch (IOException e) {
            log.error("S3 업로드 중 이미지 크기 측정 실패: {}", originalFilename, e);
        }

        if (doesObjectExist(s3Key)) {
            log.warn("S3에 동일한 파일이 이미 존재하여 업로드를 건너뜁니다. Key: {}", s3Key);
            return createImageUploadResult(finalFileName, width, height);
        }

        try {
            String encodedOriginalFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8).replace("+", "%20");
            String contentDisposition = "attachment; filename*=\"UTF-8''" + encodedOriginalFilename + "\"";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentDisposition(contentDisposition)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}, entityId: {}", originalFilename, entityId, e);
            throw new GlobalException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return createImageUploadResult(finalFileName, width, height);
    }

    protected S3UrlParts parseS3Url(String s3Url) {
        try {
            URL url = new URL(s3Url);
            String host = url.getHost();
            String path = url.getPath();
            String key = path.substring(1);

            String bucket;
            int s3Index = host.indexOf(".s3." + region + ".amazonaws.com");
            if (s3Index != -1) {
                bucket = host.substring(0, s3Index);
            } else {
                log.error("S3 URL 형식을 파싱할 수 없습니다. 호스트가 예상과 다릅니다: {}", host);
                throw new IllegalArgumentException("Invalid S3 URL format. Cannot parse bucket.");
            }

            if (!bucket.equals(bucketName)) {
                log.warn("S3 URL의 버킷({})이 현재 설정된 버킷({})과 다릅니다.", bucket, bucketName);
            }
            return new S3UrlParts(bucket, key);

        } catch (Exception e) {
            log.error("S3 URL 파싱 실패: {}", s3Url, e);
            throw new GlobalException(ErrorCode.INVALID_INPUT_VALUE, "Invalid S3 URL");
        }
    }

    protected String extractFilenameFromContentDisposition(String contentDisposition) {
        if (contentDisposition == null) {
            log.warn("Content-Disposition이 null입니다. 임시 파일명을 사용합니다.");
            return "cloned-file-" + System.currentTimeMillis();
        }

        String prefix = "filename*=\"UTF-8''";
        int startIndex = contentDisposition.indexOf(prefix);
        if (startIndex != -1) {
            String encodedName = contentDisposition.substring(startIndex + prefix.length());
            if (encodedName.endsWith("\"")) {
                encodedName = encodedName.substring(0, encodedName.length() - 1);
            }
            try {
                return URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("Content-Disposition (RFC 5987) 파일명 디코딩 실패: {}", contentDisposition, e);
            }
        }

        String fnPrefix = "filename=";
        startIndex = contentDisposition.indexOf(fnPrefix);
        if (startIndex != -1) {
            String name = contentDisposition.substring(startIndex + fnPrefix.length());
            if (name.startsWith("\"")) name = name.substring(1);
            if (name.endsWith("\"")) name = name.substring(0, name.length() - 1);
            return name.replace("+", "%20");
        }

        log.warn("Content-Disposition에서 파일명을 추출하지 못했습니다: {}. 임시 파일명을 사용합니다.", contentDisposition);
        return "cloned-file-" + System.currentTimeMillis();
    }

    protected ImageUploadResult createImageUploadResult(String fileName, Integer width, Integer height) {
        String widthStr = (width != null) ? String.valueOf(width) : null;
        String heightStr = (height != null) ? String.valueOf(height) : null;
        return new ImageUploadResult(fileName, widthStr, heightStr);
    }

    protected String generateFinalUploadFileName(ImageType imageType, String originalFilename) {
        return imageType.name() + "_" + originalFilename;
    }

    protected void validateFile(byte[] fileBytes, String originalFilename, ImageType imageType) {
        if (fileBytes == null || fileBytes.length == 0) {
            log.warn("{}: 파일이 비어있어 유효성 검사에 실패했습니다.", originalFilename);
            throw new GlobalException(ErrorCode.FILE_IS_EMPTY);
        }

        String fileExtension = getFileExtension(originalFilename);
        if (imageType.getAllowedExtensions().stream().noneMatch(ext -> ext.equalsIgnoreCase(fileExtension))) {
            log.warn("{}: 지원하지 않는 파일 확장자 '{}'입니다. 허용된 확장자: {}",
                    originalFilename, fileExtension, imageType.getAllowedExtensions());
            throw new GlobalException(ErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }

        if (fileBytes.length > imageType.getMaxSize()) {
            log.warn("{}: 파일 크기 {}가 제한 크기 {}를 초과했습니다.",
                    originalFilename, fileBytes.length, imageType.getMaxSize());
            throw new GlobalException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        if (imageType.getWidth() != null || imageType.getHeight() != null) {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
                if (image == null) {
                    log.warn("{}: 이미지 데이터를 읽을 수 없습니다. 유효한 이미지 파일이 아닐 수 있습니다.", originalFilename);
                    throw new GlobalException(ErrorCode.INVALID_IMAGE_FILE);
                }

                if (imageType.getWidth() != null && image.getWidth() != imageType.getWidth()) {
                    log.warn("{}: 넓이가 유효하지 않습니다. 현재: {}, 필요: {}",
                            originalFilename, image.getWidth(), imageType.getWidth());
                    throw new GlobalException(ErrorCode.INVALID_IMAGE_DIMENSIONS);
                }
                if (imageType.getHeight() != null && image.getHeight() != imageType.getHeight()) {
                    log.warn("{}: 높이가 유효하지 않습니다. 현재: {}, 필요: {}",
                            originalFilename, image.getHeight(), imageType.getHeight());
                    throw new GlobalException(ErrorCode.INVALID_IMAGE_DIMENSIONS);
                }
            } catch (IOException e) {
                log.error("{} 파일 유효성 검사 중 이미지 읽기 오류 발생.", originalFilename, e);
                throw new GlobalException(ErrorCode.INVALID_IMAGE_FILE);
            }
        }
    }

    protected String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }

        String cleanFilename = filename;
        int queryIndex = cleanFilename.indexOf('?');
        if (queryIndex != -1) {
            cleanFilename = cleanFilename.substring(0, queryIndex);
        }

        int lastDotIndex = cleanFilename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == cleanFilename.length() - 1) {
            return "";
        }

        return cleanFilename.substring(lastDotIndex + 1).toLowerCase();
    }


    protected boolean doesObjectExist(String key) {
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

    protected String generateS3Key(String fileName, ImageType imageType, Long entityId) {
        String basePath = imageType.getPath();
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("%s/%d/%s", basePath, entityId, encodedFileName);
    }

    protected String extractOriginalFilenameFromUrl(String imageUrl) {
        try {
            String targetUrl = imageUrl;
            URL initialUrl = new URL(imageUrl);
            String query = initialUrl.getQuery();

            if (query != null && query.contains("src=")) {
                targetUrl = Arrays.stream(query.split("&"))
                        .filter(p -> p.startsWith("src="))
                        .map(p -> p.substring(4))
                        .findFirst()
                        .map(src -> {
                            try {
                                return URLDecoder.decode(src, StandardCharsets.UTF_8);
                            } catch (Exception e) {
                                return src;
                            }
                        })
                        .orElse(imageUrl);
            }

            String path = new URL(targetUrl).getPath();
            return path.substring(path.lastIndexOf('/') + 1);

        } catch (Exception e) {
            log.warn("URL에서 파일명을 추출하는 데 실패했습니다. URL: {}. 폴백 로직을 사용합니다.", imageUrl, e);
            String urlWithoutQuery = imageUrl;
            int queryIndex = imageUrl.indexOf('?');
            if (queryIndex != -1) {
                urlWithoutQuery = imageUrl.substring(0, queryIndex);
            }
            return urlWithoutQuery.substring(urlWithoutQuery.lastIndexOf('/') + 1);
        }
    }
}