package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.error.utils.ErrorUtil;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import io.micrometer.core.instrument.config.validate.ValidationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;


@Profile(value = {"local"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "gyun.sample")
public class ExceptionAdvice extends RestApiControllerAdvice {

    private final ErrorUtil errorUtil;

    public ExceptionAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher, ErrorUtil errorUtil) {
        super(objectMapper, applicationEventPublisher);
        this.errorUtil = errorUtil;
    }

    // Business Exception Catch
    @ExceptionHandler(value = GlobalException.class)
    protected ResponseEntity<String> processCommonException(GlobalException commonException, @CurrentAccount CurrentAccountDTO account) {
        ErrorCode errorCode = commonException.getErrorCode();
        // Event - Log
        if (!account.role().equals(AccountRole.GUEST)) {
            sendLogEvent(commonException, account);
        } else {
            sendLogEventNoAccount(commonException);
        }
        return createFailRestResponse(errorCode.getErrorResponse());
    }

    // Validation Exception Catch
    @ExceptionHandler(value = ValidationException.class)
    protected ResponseEntity<String> processValidationException(ValidationException validationException, @CurrentAccount CurrentAccountDTO account) {
        ErrorCode errorCode = errorUtil.findErrorCodeOnMessage(validationException.getMessage(), ErrorCode.CONSTRAINT_PROCESS_FAIL);
        GlobalException globalException = new GlobalException(errorCode, validationException);

        // Event - Log
        if (!account.role().equals(AccountRole.GUEST)) {
            sendLogEvent(globalException, account);
        } else {
            sendLogEventNoAccount(globalException);
        }

        return createFailRestResponse(new HashMap<>() {{
            put("error", errorCode.getErrorMap());
        }});
    }
}
