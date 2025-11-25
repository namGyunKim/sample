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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
    private final ApplicationEventPublisher eventPublisher;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Operation(summary = "구글 로그인 리다이렉트 URL 요청")
    @GetMapping("/google/login")
    public RedirectView googleLogin() {
        String url = socialServiceFactory.getService(MemberType.GOOGLE).getLoginRedirectUrl();
        return new RedirectView(url);
    }

    @Operation(summary = "구글 로그인 콜백")
    @GetMapping("/google/redirect")
    public String googleRedirect(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) {

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

            securityContextRepository.saveContext(securityContext, request, response);

            // 소셜 로그인 로그 이벤트 발행
            eventPublisher.publishEvent(MemberActivityEvent.of(
                    member.getLoginId(),
                    member.getId(),
                    member.getLoginId(), // executorId = 본인
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