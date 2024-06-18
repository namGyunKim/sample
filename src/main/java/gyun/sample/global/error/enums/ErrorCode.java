package gyun.sample.global.error.enums;

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
    JWT_TOKEN_EXPIRED("1001", "JWT 토큰 유효기간 만료"),
    JWT_INVALID("1002", "JWT 토큰이 유효하지 않음"),
    JWT_REFRESH_INVALID("1003", "Refresh 토큰이 유효하지 않음"),
    ACCESS_DENIED("1004", "권한이 없습니다."),
    NOT_MATCH_PASSWORD("1005", "비밀번호가 일치하지 않습니다."),
    JWT_UNKNOWN_ERROR("1006", "JWT 토큰 에러"),
    JWT_SIGNATURE_ERROR("1007", "JWT 시그니처 에러(시크릿 키)"),

    // === MEMBER (1100) ===
    ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID("1101", "이미 등록된 아이디입니다."),
    NOT_EXIST_MEMBER("1102", "존재하지 않는 회원입니다."),
    INACTIVE_MEMBER("1103", "탈퇴한 회원입니다."),
    PASSWORD_INVALID("1104", "비밀번호는 8~15자리의 영문, 숫자, 특수문자 조합이어야 합니다."),
    ALREADY_REGISTERED_MEMBER_BY_NICK_NAME("1105", "이미 등록된 닉네임입니다."),

    // === SOCIAL (1200) ===
    KAKAO_API_GET_CODE_ERROR("1201", "카카오 API Get Code Error"),
    KAKAO_API_GET_TOKEN_ERROR("1202", "카카오 API Get Token Error"),
    KAKAO_API_GET_INFORMATION_ERROR("1203", "카카오 API Get Information Error"),
    KAKAO_API_LOGOUT_ERROR("1204", "카카오 API Logout Error"),
    KAKAO_API_UNLINK_ERROR("1205", "카카오 API Unlink Error"),

    // === BOARD (2000) ===
    GUEST_NOT_WRITE("2001", "비회원은 익명 게시판에만 글을 작성할 수 있습니다."),
    NOT_EXIST_BOARD("2002", "존재하지 않는 게시글입니다."),

    // === OTHER (9000) ===
    JSON_PROCESS_FAIL("9001", "Json 파일을 처리하는데 실패했습니다."),
    CONSTRAINT_PROCESS_FAIL("9002", "정보가 서로 일치하지 않습니다."),

    FAILED("9999", "Unexpected Error");

    private static final Map<String, ErrorCode> errorMap =
            Arrays.stream(values()).collect(Collectors.toMap(ErrorCode::getCode, e -> e));
    private final String code;
    private final String errorMessage;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public static ErrorCode findByCode(String code) {
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
