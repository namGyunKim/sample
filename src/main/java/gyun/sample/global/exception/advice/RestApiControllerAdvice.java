package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.event.ExceptionEvent;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.payload.response.BindingResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;

// 예외 처리
public class RestApiControllerAdvice extends RestApiController {

    private final ApplicationEventPublisher applicationEventPublisher;


    public RestApiControllerAdvice(ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(objectMapper);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // 컨트롤러를 거친 이후 Event - Log
    protected void sendLogEvent(GlobalException exception, HttpServletRequest httpServletRequest) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEvent(exception, httpServletRequest));
    }

    protected void sendLogEvent(BindingResultResponse response, HttpServletRequest httpServletRequest) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEventBinding(response, httpServletRequest));
    }

}
