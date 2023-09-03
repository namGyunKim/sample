package namGyun.sample.global.exception.enums;

import namGyun.sample.global.exception.payload.response.ErrorResult;
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
    JWT_TOKEN_EXPIRED("1001", "JWT TOKEN EXPIRED"),
    JWT_VERIFICATION_FAIL("1002", "JWT TOKEN VERIFICATION FAILED"),
    JWT_EXCEPTION_FAIL("1003", "JWT TOKEN EXCEPTION FAIL"),
    REFRESH_TOKEN_NOT_FOUND("1004", "Can't find jwt refresh token."),
    UNAUTHORIZED("1005", "UNAUTHORIZED"),
    ACCESS_DENIED("1006", "ACCESS_DENIED"),
    PASSWORD_DOES_NOT_MATCH("1007", "The password does not match."),
    UNKNOWN_ACCOUNT_AUTHORITY("1008", "Your authority is unknown."),
    ACCOUNT_MISSING_OR_DELETED("1009", "The account is missing or has been deleted."),
    AUTHORITY_HAVE_NO_CONTROL_OVER("1010", "Your authority has no control over to do this behavior."),
    PARENTS_ACCOUNT_IS_INACTIVATED("1011", "Your parent account is inactivated."),
    TERMS_OF_USE_REQUIRED_ITEM("1012", "Terms of use are required to be agreed upon."),
    PRIVACY_POLICY_REQUIRED_ITEM("1013", "Privacy policy are required to be agreed upon."),
    COURIER_SHIPPING_TERMS_REQUIRED_ITEM("1014", "Courier shipping terms are required to be agreed upon."),
    // === MEMBER (1100) ============================================================================================================
    ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID("1101", "이미 등록된 아이디입니다."),

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

    public void setCode(String code) {
        this.code = code;
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
            jsonObject.put("message", errorMessage);
        } catch (JSONException ignore) {
        }
        return jsonObject;
    }

    public String getCustomErrorCodeStr() {
        return "ERRORCODE_" + this.code;
    }

}
