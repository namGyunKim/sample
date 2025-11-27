package gyun.sample.global.security.handler;

import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * 로그인 실패 핸들러
 * - 로그인 실패 시 로그 이벤트를 발행하고 에러 페이지로 리다이렉트합니다.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String loginId = request.getParameter("loginId");
        String clientIp = UtilService.getClientIp(request);

        // 실패한 계정 조회 (존재하지 않는 아이디일 경우 memberId는 null)
        Long memberId = null;
        if (loginId != null) {
            Optional<Member> memberOptional = memberRepository.findByLoginId(loginId);
            if (memberOptional.isPresent()) {
                memberId = memberOptional.get().getId();
            }
        }

        // [수정] 예외 타입별 상세 메시지 생성
        String detailMessage = getFailureMessage(exception);

        // 로그인 실패 로그 이벤트 발행
        eventPublisher.publishEvent(MemberActivityEvent.of(
                loginId != null ? loginId : "UNKNOWN",
                memberId, // 회원이 없으면 null 저장
                loginId != null ? loginId : "UNKNOWN", // 수행자는 시도한 사람
                LogType.LOGIN_FAIL,
                detailMessage,
                clientIp
        ));

        // 기본 실패 URL 설정 및 리다이렉트 수행
        setDefaultFailureUrl("/account/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }

    // 예외 타입에 따른 한글 메시지 반환
    private String getFailureMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "비밀번호 불일치";
        } else if (exception instanceof UsernameNotFoundException) {
            return "계정 없음";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            return "내부 시스템 에러";
        } else if (exception instanceof LockedException) {
            return "계정 잠김";
        } else if (exception instanceof DisabledException) {
            return "계정 비활성화";
        } else if (exception instanceof AccountExpiredException) {
            return "계정 만료";
        } else if (exception instanceof CredentialsExpiredException) {
            return "비밀번호 만료";
        } else {
            return "로그인 실패: " + exception.getMessage();
        }
    }
}