package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.event.ExceptionEvent;
import gyun.sample.global.exception.GlobalException;
import org.springframework.context.ApplicationEventPublisher;

public class RestApiControllerAdvice extends RestApiController {

    private final ApplicationEventPublisher applicationEventPublisher;


    public RestApiControllerAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected void sendLogEvent(GlobalException aException, CurrentAccountDTO account) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEvent(aException, account));
    }

    protected void sendLogEventNoAccount(GlobalException aException) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEventNoAccount(aException));
    }
}
