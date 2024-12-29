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

    // === AUTH (1000) ===
    JWT_EXPIRED("1001", "JWT 토큰 유효기간 만료"),
    JWT_INVALID("1002", "JWT 토큰이 유효하지 않음"),
    JWT_REFRESH_INVALID("1003", "Refresh 토큰이 유효하지 않음"),
    ACCESS_DENIED("1004", "권한이 없습니다."),
    PASSWORD_NOT_MATCH("1005", "비밀번호가 일치하지 않습니다."),
    JWT_UNKNOWN_ERROR("1006", "JWT 토큰 에러"),
    JWT_SIGNATURE_ERROR("1007", "JWT 시그니처 에러(시크릿 키)"),
    // === MEMBER (1100) ===
    MEMBER_NOT_EXIST("1101", "존재하지 않는 회원입니다."),
    MEMBER_INACTIVE("1102", "비활성화된 계정입니다."),
    PASSWORD_INVALID("1103", "비밀번호 유효성 검토"),
    MEMBER_IMAGE_NOT_EXIST("1104", "존재하지 않는 회원 이미지입니다."),

    // === SOCIAL (1200) ===
    KAKAO_API_GET_CODE_ERROR("1201", "카카오 API Get Code Error"),
    KAKAO_API_GET_TOKEN_ERROR("1202", "카카오 API Get Token Error"),
    KAKAO_API_GET_INFORMATION_ERROR("1203", "카카오 API Get Information Error"),
    KAKAO_API_LOGOUT_ERROR("1204", "카카오 API Logout Error"),
    KAKAO_API_UNLINK_ERROR("1205", "카카오 API Unlink Error"),

    // === BOARD (2000) ===
    COMMENT_NOT_EXIST("2001", "존재하지 않는 댓글입니다."),
    COMMENT_INACTIVE("2002", "비활성화된 댓글입니다."),
    BOARD_INACTIVE("2003", "비활성화된 게시글입니다."),
    FREE_BOARD_NOT_EXIST("2004", "존재하지 않는 자유 게시글 입니다."),
    BOARD_NOT_EXIST("2005", "존재하지 않는 게시글입니다."),
    QUESTION_BOARD_NOT_EXIST("2006", "존재하지 않는 질문 게시글 입니다."),

    // === OTHER (9000) ===
    JSON_PROCESS_FAIL("9001", "Json 파일을 처리하는데 실패했습니다."),
    COUNT_FETCH_ERROR("9002", "개수 조회에 실패하였습니다."),
    DATA_ACCESS_ERROR("9003", "데이터베이스 접근 에러"),
    MAX_UPLOAD_SIZE_EXCEEDED("9004", "파일 업로드 크기 제한 초과"),
    INPUT_VALUE_INVALID("9005", "입력값이 올바르지 않습니다."),
    PAGE_NOT_EXIST("9006", "페이지를 찾을 수 없습니다."),
    METHOD_NOT_SUPPORTED("9007", "지원하지 않는 메소드입니다."),
    ARGUMENT_INVALID("9008", "잘못된 인자값입니다."),
    REFLECTION_ERROR("9009", "리플렉션 에러"),
    FILE_UPLOAD_ERROR("9010", "파일 업로드 에러"),
    FILE_NOT_FOUND("9011", "파일을 찾을 수 없습니다."),
    FILE_FORMAT_INVALID("9012", "잘못된 파일 형식입니다."),
    FILE_DOWNLOAD_ERROR("9013", "파일 다운로드 에러"),
    FILE_TOO_LARGE("9014", "파일 크기가 너무 큽니다."),
    VERIFICATION_CODE_INVALID("9015", "인증 코드가 일치하지 않습니다."),
    UPLOAD_FAILED("9016", "파일 업로드에 실패하였습니다."),
    FILTER_NOT_EXIST("9017", "필터가 존재하지 않습니다."),
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

    public ErrorResult getErrorResponseWithSocial(String detailMessage) {
        return new ErrorResult(code, detailMessage);
    }

    public Map<String, String> getErrorMap() {
        return new HashMap<>() {{
            put("code", code);
            put("message", errorMessage);
        }};
    }
}
