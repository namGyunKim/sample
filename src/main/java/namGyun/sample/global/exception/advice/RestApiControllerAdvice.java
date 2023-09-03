package namGyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import namGyun.sample.domain.account.dto.CurrentAccountDTO;
import namGyun.sample.global.api.RestApiController;
import namGyun.sample.global.event.ExceptionEvent;
import namGyun.sample.global.exception.GlobalException;
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
