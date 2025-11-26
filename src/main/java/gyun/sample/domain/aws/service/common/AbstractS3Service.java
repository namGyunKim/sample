package gyun.sample.domain.aws.service.common;

import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.aws.payload.dto.ImageUploadResult;
import gyun.sample.domain.aws.payload.dto.S3UrlParts;
import gyun.sample.domain.aws.service.S3Service;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * S3Service의 공통 로직을 구현하는 추상 클래스.
 * Java 21 스타일에 맞춰 HttpClient 등 모던 API 적용
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractS3Service implements S3Service {

    protected final S3Client s3Client;

    @Autowired
    private Environment environment;

    @Value("${s3.bucket}")
    private String defaultBucketName;

    protected String bucketName;

    @Value("${aws.region}")
    protected String region;

    // Java 11+ HttpClient (재사용 권장)
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    @Value("${s3.bucket-local:}")
    private String localBucketName;

    @PostConstruct
    public void init() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isLocal = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equalsIgnoreCase("local"));

        if (isLocal && localBucketName != null && !localBucketName.isBlank()) {
            this.bucketName = localBucketName;
            log.info("로컬 프로필이 감지되어 로컬 버킷({})을 사용합니다.", this.bucketName);
        } else {
            this.bucketName = defaultBucketName;
            log.info("운영 버킷({})을 사용합니다.", this.bucketName);
        }
    }

    protected abstract void validateImageType(ImageType imageType);

    @Override
    public ImageUploadResult uploadImage(MultipartFile file, ImageType imageType, Long entityId) {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        try {
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
            log.info("이미지 다운로드 시도 (HttpClient). URL: {}", imageUrl);
            validateImageType(imageType);

            // Java 11+ HttpClient 사용 (Modern I/O)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                log.error("URL에서 이미지 다운로드 실패. URL: {}, 응답 코드: {}", imageUrl, response.statusCode());
                throw new GlobalException(ErrorCode.FILE_DOWNLOAD_FAILED);
            }

            byte[] fileBytes = response.body();
            // 헤더 값 조회 시 대소문자 무관하게 처리됨
            String contentType = response.headers().firstValue("Content-Type").orElse(null);

            String originalFilename = extractOriginalFilenameFromUrl(imageUrl);

            if (getFileExtension(originalFilename).isEmpty()) {
                String extension = getExtensionFromContentType(contentType);
                if (extension != null) {
                    originalFilename = originalFilename + extension;
                } else {
                    originalFilename = originalFilename + ".png";
                }
            }

            return uploadToS3Internal(fileBytes, originalFilename, imageType, entityId);

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
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
        // 기존 로직 유지 (S3 CopyObject 활용)
        log.info("S3-to-S3 복사 시작. Source URL: {}, DestType: {}, DestEntityId: {}",
                sourceS3Url, destinationImageType.name(), destinationEntityId);

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
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.FILE_NOT_FOUND, "Source S3 file not found or inaccessible.");
        }

        String finalFileName = generateFinalUploadFileName(destinationImageType, originalFilename);
        String destinationKey = generateS3Key(finalFileName, destinationImageType, destinationEntityId);

        if (doesObjectExist(destinationKey)) {
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

        } catch (Exception e) {
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

    // === Protected Helper Methods ===

    protected ImageUploadResult uploadToS3Internal(byte[] fileBytes, String originalFilename, ImageType imageType, Long entityId) {
        validateFile(fileBytes, originalFilename, imageType);

        String finalFileName = generateFinalUploadFileName(imageType, originalFilename);
        String s3Key = generateS3Key(finalFileName, imageType, entityId);

        Integer width = null;
        Integer height = null;

        // 이미지 크기 읽기 (ByteArrayInputStream은 close()가 no-op이므로 try-with-resources 필수는 아니지만 관례상 유지)
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            BufferedImage image = ImageIO.read(is);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }
        } catch (IOException e) {
            log.warn("이미지 크기 측정 실패: {}", originalFilename);
        }

        if (doesObjectExist(s3Key)) {
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
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw new GlobalException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return createImageUploadResult(finalFileName, width, height);
    }

    protected S3UrlParts parseS3Url(String s3Url) {
        try {
            URI uri = URI.create(s3Url);
            URL url = uri.toURL();
            String host = url.getHost();
            String path = url.getPath();
            String key = path.substring(1);

            String bucket;
            int s3Index = host.indexOf(".s3." + region + ".amazonaws.com");
            if (s3Index != -1) {
                bucket = host.substring(0, s3Index);
            } else {
                throw new IllegalArgumentException("Invalid S3 URL format.");
            }
            return new S3UrlParts(bucket, key);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INPUT_VALUE_INVALID, "Invalid S3 URL");
        }
    }

    protected String extractFilenameFromContentDisposition(String contentDisposition) {
        if (contentDisposition == null) {
            return "cloned-file-" + System.currentTimeMillis();
        }

        String prefix = "filename*=\"UTF-8''";
        int startIndex = contentDisposition.indexOf(prefix);
        if (startIndex != -1) {
            String encodedName = contentDisposition.substring(startIndex + prefix.length());
            if (encodedName.endsWith("\"")) encodedName = encodedName.substring(0, encodedName.length() - 1);
            try {
                return URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
            } catch (Exception e) { /* ignore */ }
        }

        String fnPrefix = "filename=";
        startIndex = contentDisposition.indexOf(fnPrefix);
        if (startIndex != -1) {
            String name = contentDisposition.substring(startIndex + fnPrefix.length());
            if (name.startsWith("\"")) name = name.substring(1);
            if (name.endsWith("\"")) name = name.substring(0, name.length() - 1);
            return name.replace("+", "%20");
        }
        return "cloned-file-" + System.currentTimeMillis();
    }

    protected ImageUploadResult createImageUploadResult(String fileName, Integer width, Integer height) {
        return new ImageUploadResult(fileName,
                width != null ? String.valueOf(width) : null,
                height != null ? String.valueOf(height) : null);
    }

    protected String generateFinalUploadFileName(ImageType imageType, String originalFilename) {
        return imageType.name() + "_" + originalFilename.replaceAll("\\s", "");
    }

    protected void validateFile(byte[] fileBytes, String originalFilename, ImageType imageType) {
        if (fileBytes == null || fileBytes.length == 0) throw new GlobalException(ErrorCode.FILE_IS_EMPTY);

        String fileExtension = getFileExtension(originalFilename);
        imageType.isExtensionAllowed(fileExtension);

        if (fileBytes.length > imageType.getMaxSize()) throw new GlobalException(ErrorCode.FILE_SIZE_EXCEEDED);

        if (imageType.getWidth() != null || imageType.getHeight() != null) {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
                if (image == null) throw new GlobalException(ErrorCode.INVALID_IMAGE_FILE);

                if ((imageType.getWidth() != null && image.getWidth() != imageType.getWidth()) ||
                        (imageType.getHeight() != null && image.getHeight() != imageType.getHeight())) {
                    throw new GlobalException(ErrorCode.INVALID_IMAGE_DIMENSIONS);
                }
            } catch (IOException e) {
                throw new GlobalException(ErrorCode.INVALID_IMAGE_FILE);
            }
        }
    }

    protected String getFileExtension(String filename) {
        if (filename == null) return "";
        String clean = filename.split("\\?")[0];
        int lastDot = clean.lastIndexOf('.');
        if (lastDot == -1 || lastDot == clean.length() - 1) return "";
        return clean.substring(lastDot + 1).toLowerCase();
    }

    protected String getExtensionFromContentType(String contentType) {
        if (contentType == null) return null;
        String mime = contentType.split(";")[0].trim().toLowerCase();
        return switch (mime) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case "image/bmp" -> ".bmp";
            case "image/svg+xml" -> ".svg";
            default -> null;
        };
    }

    protected boolean doesObjectExist(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    protected String generateS3Key(String fileName, ImageType imageType, Long entityId) {
        String basePath = imageType.getPath().endsWith("/") ?
                imageType.getPath().substring(0, imageType.getPath().length() - 1) : imageType.getPath();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("%s/%d/%s", basePath, entityId, encodedFileName);
    }

    protected String extractOriginalFilenameFromUrl(String imageUrl) {
        try {
            String targetUrl = imageUrl;
            URI uri = URI.create(imageUrl);
            String query = uri.getQuery();

            if (query != null && query.contains("src=")) {
                targetUrl = Arrays.stream(query.split("&"))
                        .filter(p -> p.startsWith("src="))
                        .map(p -> p.substring(4))
                        .findFirst()
                        .map(src -> URLDecoder.decode(src, StandardCharsets.UTF_8))
                        .orElse(imageUrl);
            }
            String path = URI.create(targetUrl).getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (Exception e) {
            String clean = imageUrl.split("\\?")[0];
            return clean.substring(clean.lastIndexOf('/') + 1);
        }
    }
}