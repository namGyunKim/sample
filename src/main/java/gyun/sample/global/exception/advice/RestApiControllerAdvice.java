package gyun.sample.global.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.event.ExceptionEvent;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
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
    protected void sendLogEvent(GlobalException exception, CurrentAccountDTO account, HttpServletRequest httpServletRequest) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEvent(exception, account,httpServletRequest));
    }

    // 컨트롤러를 거치기 전 JWT 관련 이슈가 터지면 error 컨트롤러로 보내서 해당 Event - Log
    protected void sendLogEvent(JWTInterceptorException exception,HttpServletRequest httpServletRequest) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEventNoAccount(exception,httpServletRequest));
    }

    // 컨트롤러를 거치기 전 바인딩 리절트 관련 이슈가 터지면 error 컨트롤러로 보내서 해당 Event - Log
    protected void sendLogEvent(BindingResultResponse response) {
        applicationEventPublisher.publishEvent(ExceptionEvent.createExceptionEventBinding(response));
    }
}
