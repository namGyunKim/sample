package gyun.sample.domain.social.api;

import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.social.google.service.GoogleSocialService;
import gyun.sample.domain.social.service.SocialServiceFactory;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.security.PrincipalDetails;
import gyun.sample.global.utils.UtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "SocialController", description = "소셜 로그인 뷰/API")
@Controller
@RequestMapping("/social")
@RequiredArgsConstructor
@Slf4j
public class SocialController {

    private final SocialServiceFactory socialServiceFactory;
    private final GoogleSocialService googleSocialService;
    private final ApplicationEventPublisher eventPublisher; // [추가] 이벤트 발행기

    @Operation(summary = "구글 로그인 리다이렉트 URL 요청", description = "구글 로그인 페이지로 이동합니다.")
    @GetMapping("/google/login")
    public RedirectView googleLogin() {
        String url = socialServiceFactory.getService(MemberType.GOOGLE).getLoginRedirectUrl();
        return new RedirectView(url);
    }

    @Operation(summary = "구글 로그인 콜백", description = "Google 리다이렉션으로 호출되며, 성공 시 세션을 발급하고 메인 페이지로 리다이렉트합니다.")
    @GetMapping("/google/redirect")
    public String googleRedirect(@RequestParam String code, HttpServletRequest request) {

        try {
            Member member = googleSocialService.getMemberBySocialCode(code);

            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails,
                    null,
                    principalDetails.getAuthorities()
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // [추가] 소셜 로그인 로그 이벤트 발행
            // (GoogleSocialService 내부에서 신규/기존 여부를 판단하지만, 여기서는 통합 로그인 성공 로그를 남김)
            // 신규 가입 로그는 GoogleSocialService 내부에서 별도로 발행하거나, 여기서 판단 로직을 추가할 수 있음.
            // 현재 구조상 서비스 레이어에서 회원가입 여부를 판단하고 있으므로, 서비스 내에서 가입 로그를 찍는 것이 좋으나
            // 편의상 여기서는 '소셜 로그인 성공'으로 통일합니다.
            eventPublisher.publishEvent(MemberActivityEvent.of(
                    member.getLoginId(),
                    member.getId(),
                    LogType.LOGIN,
                    "GOOGLE 소셜 로그인 성공",
                    UtilService.getClientIp(request)
            ));

            log.info("Google 소셜 로그인 성공 및 세션 등록: {}", member.getLoginId());
            return "redirect:/";

        } catch (GlobalException e) {
            log.error("소셜 로그인 실패: {}", e.getErrorDetailMessage());
            return "redirect:/account/login?error=social";
        } catch (Exception e) {
            log.error("예상치 못한 소셜 로그인 오류: {}", e.getMessage(), e);
            return "redirect:/account/login?error=unknown";
        }
    }
}