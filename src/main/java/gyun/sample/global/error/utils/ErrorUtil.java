package gyun.sample.global.error.utils;

import gyun.sample.global.error.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
public class ErrorUtil {

    private static final String TARGET_STRING = "ERRORCODE_";

    //메시지 내에 커스텀 CODE 있는지 찾음.
    public ErrorCode findErrorCodeOnMessage(String message, ErrorCode errorCode) {
        String code = getCodeWithMessage(message);
        if (!StringUtils.hasText(code)) {
            return errorCode;
        }
        return ErrorCode.getByCode(code) != null ? ErrorCode.getByCode(code) : errorCode;
    }

    // Custom Error Code 분리
    private String getCodeWithMessage(String message) {
        Pattern pattern = Pattern.compile("ERRORCODE_[0-9]{4}");
        Matcher matcher = pattern.matcher(message);

        if (!matcher.find()) return "";
        return matcher.group().replace(TARGET_STRING, "").trim();
    }
}
