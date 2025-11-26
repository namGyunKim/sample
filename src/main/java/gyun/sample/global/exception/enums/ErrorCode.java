package gyun.sample.global.exception.enums;

import gyun.sample.global.exception.payload.response.ErrorResult;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum ErrorCode {

    //  === COMMON (0000) ===
    REQUEST_BINDING_RESULT("0001", "Request 바인딩 에러"),
    INPUT_VALUE_INVALID("0002", "유효하지 않은 입력 값입니다."),
    INVALID_PARAMETER("0003", "잘못된 파라미터입니다."),
    INTERNAL_SERVER_ERROR("0004", "내부 서버 오류"),
    METHOD_NOT_SUPPORTED("0005", "지원하지 않는 메소드입니다."),
    PAGE_NOT_EXIST("0006", "페이지를 찾을 수 없습니다."), // 추가됨: 404 처리용

    // === AUTH (1000) ===
    ACCESS_DENIED("1004", "권한이 없습니다."),
    SOCIAL_TOKEN_ERROR("1006", "소셜 토큰 에러"),

    // === MEMBER (1100) ===
    MEMBER_NOT_EXIST("1101", "존재하지 않는 회원입니다."),
    MEMBER_INACTIVE("1102", "비활성화된 계정입니다."),
    MEMBER_ALREADY_EXIST("1103", "이미 존재하는 회원입니다."),

    // === SOCIAL (1200) ===
    GOOGLE_API_GET_CODE_ERROR("1206", "구글 API Get Code Error"),
    GOOGLE_API_GET_TOKEN_ERROR("1207", "구글 API Get Token Error"),
    GOOGLE_API_GET_INFORMATION_ERROR("1208", "구글 API Get Information Error"),
    GOOGLE_API_UNLINK_ERROR("1209", "구글 API Unlink Error"),

    // === FILE/IMAGE (5000) ===
    INVALID_IMAGE_FILE("5001", "유효하지 않은 이미지 파일입니다."),
    INVALID_IMAGE_DIMENSIONS("5002", "이미지 규격이 올바르지 않습니다."), // 추가됨: 이미지 크기 검증용
    FILE_SIZE_EXCEEDED("5003", "파일 크기가 제한을 초과했습니다."),
    FILE_IS_EMPTY("5004", "파일이 비어있습니다."),
    UNSUPPORTED_FILE_EXTENSION("5005", "지원하지 않는 파일 확장자입니다."),
    FILE_UPLOAD_FAILED("5007", "파일 업로드에 실패했습니다."),
    FILE_DOWNLOAD_FAILED("5008", "파일 다운로드에 실패했습니다."),
    FILE_NOT_FOUND("9011", "파일을 찾을 수 없습니다."),

    // === OTHER (9000) ===
    DATA_ACCESS_ERROR("9003", "데이터베이스 접근 에러"),
    FAILED("9999", "Unexpected Error");

    private static final Map<String, ErrorCode> errorMap =
            Arrays.stream(values()).collect(Collectors.toMap(ErrorCode::getCode, e -> e));
    private final String code;
    private final String errorMessage;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public static ErrorCode getByCode(String code) {
        return errorMap.get(code);
    }

    public ErrorResult getErrorResponse() {
        return new ErrorResult(code, errorMessage);
    }

    public Map<String, String> getErrorMap() {
        return new HashMap<>() {{
            put("code", code);
            put("message", errorMessage);
        }};
    }
}