package gyun.sample.global.error.enums;

import gyun.sample.global.exception.payload.response.ErrorResult;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum ErrorCode {

    //  === COMMON (0000) ============================================================================================================
    REQUEST_BINDING_RESULT("0001", "리퀘스트 데이터 문제"),
    // === AUTH (1000) ============================================================================================================
    JWT_TOKEN_EXPIRED("1001", "JWT 토큰 유효기간 만료"),
    JWT_INVALID("1002", "JWT 토큰이 유효하지 않음"),
    JWT_REFRESH_INVALID("1003", "Refresh 토큰이 유효하지 않음"),
    ACCESS_DENIED("1004", "권한이 없습니다."),
    NOT_MATCH_PASSWORD("1005", "비밀번호가 일치하지 않습니다."),
    // === MEMBER (1100) ============================================================================================================
    ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID("1101", "이미 등록된 아이디입니다."),
    NOT_EXIST_MEMBER("1102", "존재하지 않는 회원입니다."),


    // === other (9000) ============================================================================================================
    JSON_PROCESS_FAIL("9001", "Json 파일을 처리하는데 실패했습니다. "),
    CONSTRAINT_PROCESS_FAIL("9002", "정보가 서로 일치하지 않습니다."),


    FAILED("9999", "Unexpected Error");

    private static final Map<String, ErrorCode> errorMap =
            Arrays.stream(values()).collect(Collectors.toMap(ErrorCode::getCode, e -> e));
    private String code;
    private String errorMessage;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public static ErrorCode findByCode(String code) {
        return errorMap.get(code);
    }

    public String getCode() {
        return code;
    }
    public String getErrorMessage() {
        return errorMessage;
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

    public Map<String, String> getErrorMap(String detail) {
        return new HashMap<>() {{
            put("code", code);
            put("message", errorMessage);
            put("detail", detail);
        }};
    }

    public String getErrorString() {
        return getErrorJson().toString();
    }

    public JSONObject getErrorJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("errorMessage", errorMessage);
        } catch (JSONException ignore) {
        }
        return jsonObject;
    }

    public String getCustomErrorCodeStr() {
        return "ERRORCODE_" + this.code;
    }

}
