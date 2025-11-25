package gyun.sample.global.security.handler;

import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.global.security.PrincipalDetails;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. 로그인 사용자 정보 획득
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        // 2. 로그인 로그 이벤트 발행
        // 로그인 주체(executor)는 본인임
        eventPublisher.publishEvent(MemberActivityEvent.of(
                principal.getUsername(),
                principal.getId(),
                principal.getUsername(), // executorId = 본인
                LogType.LOGIN,
                "일반 로그인 성공",
                UtilService.getClientIp(request)
        ));

        // 3. 메인 페이지로 리다이렉트
        response.sendRedirect("/");
    }
}