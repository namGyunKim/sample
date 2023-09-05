package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.event.ExceptionEvent;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import org.springframework.context.ApplicationEventPublisher;

public class RestApiControllerAdvice extends RestApiController {

    private final ApplicationEventPublisher applicationEventPublisher;


    public RestApiControllerAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected void sendLogEvent(GlobalException exception, CurrentAccountDTO account) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEvent(exception, account));
    }

    protected void sendLogEvent(JWTInterceptorException exception) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEventNoAccount(exception));
    }
}
