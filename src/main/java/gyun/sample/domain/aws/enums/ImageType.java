package gyun.sample.domain.aws.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ImageType {
    // === Member Related (New) ===
    MEMBER_PROFILE("회원 프로필 이미지", "member/profile/");

    // --- Static Map ---
    private static final Map<String, ImageType> NAME_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(
                    type -> type.name().toUpperCase(), // Key: Enum 이름 (대문자)
                    Function.identity()               // Value: Enum 상수 자신
            ));
    // Member
    private static final List<ImageType> MEMBER_UPLOAD_ALLOWED_TYPES = List.of(
            MEMBER_PROFILE
    );
    // --- Fields ---
    private final String description;
    private final String path;
    private final Integer width;
    private final Integer height;
    private final List<String> allowedExtensions;
    private final long maxSize;

    // --- Constructors ---
    ImageType(String description, String path) {
        this(description, path, null, null, List.of("jpg", "jpeg", "png", "webp"), 10 * 1024 * 1024L);
    }

    // --- Static Helper Methods ---
    @JsonCreator
    public static ImageType create(String requestValue) {
        if (requestValue == null) {
            return null;
        }
        return NAME_MAP.get(requestValue.toUpperCase());
    }

    public static ImageType getByName(String name) {
        return create(name);
    }

    // --- Allowed Type Lists ---

    public static List<ImageType> getAllTypes() {
        return List.of(values());
    }


    // --- Static Mapping/Validation Methods ---

    // Member (S3MemberService에서 필요)
    public static void validateMemberUploadType(ImageType type) {
        if (type == null || !MEMBER_UPLOAD_ALLOWED_TYPES.contains(type)) {
            // [수정] ErrorCode.INVALID_INPUT_VALUE -> ErrorCode.INPUT_VALUE_INVALID로 변경
            throw new GlobalException(ErrorCode.INPUT_VALUE_INVALID, "회원 업로드에 허용되지 않는 이미지 타입입니다: " + type);
        }
    }

    public void isExtensionAllowed(String extension) {
        if (extension == null || extension.isBlank()) {
            // null이나 공백일 경우 예외를 던짐
            throw new GlobalException(ErrorCode.UNSUPPORTED_FILE_EXTENSION, "파일 확장자가 없습니다.");
        }
        // 대소문자 구분 없이 비교
        boolean match = this.allowedExtensions.stream()
                .anyMatch(allowed -> allowed.equalsIgnoreCase(extension));

        if (!match) {
            // ErrorCode 변경 및 메시지 포맷팅
            String allowed = String.join(", ", this.allowedExtensions);
            throw new GlobalException(ErrorCode.UNSUPPORTED_FILE_EXTENSION, "허용되지 않는 파일 확장자입니다. (" + extension + "). 허용된 확장자: " + allowed);
        }
    }
}