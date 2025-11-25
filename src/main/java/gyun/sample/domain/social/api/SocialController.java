package gyun.sample.domain.social.api;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.social.google.service.GoogleSocialService;
import gyun.sample.domain.social.service.SocialServiceFactory;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // [추가] Spring Security 6에서는 명시적으로 세션에 저장하기 위해 Repository가 필요함
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    /**
     * 구글 로그인 요청 URL로 리다이렉트
     */
    @Operation(summary = "구글 로그인 리다이렉트 URL 요청", description = "구글 로그인 페이지로 이동합니다.")
    @GetMapping("/google/login")
    public RedirectView googleLogin() {
        String url = socialServiceFactory.getService(MemberType.GOOGLE).getLoginRedirectUrl();
        return new RedirectView(url);
    }

    /**
     * Google OAuth 2.0 인증 Code를 받아 세션을 발급하는 Endpoint
     */
    @Operation(summary = "구글 로그인 콜백", description = "Google 리다이렉션으로 호출되며, 성공 시 세션을 발급하고 메인 페이지로 리다이렉트합니다.")
    @GetMapping("/google/redirect")
    public String googleRedirect(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) {

        try {
            // 1. 소셜 로그인 처리 및 Member 객체 가져오기
            Member member = googleSocialService.getMemberBySocialCode(code);

            // 2. Spring Security 세션 수동 생성 준비
            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails,
                    null,
                    principalDetails.getAuthorities()
            );

            // 3. SecurityContext 생성 및 설정
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // [중요] 4. 세션에 SecurityContext 저장 (Spring Security 6 필수)
            // 이 코드가 없으면 리다이렉트 후 로그인 상태가 유지되지 않습니다.
            securityContextRepository.saveContext(securityContext, request, response);

            log.info("Google 소셜 로그인 성공 및 세션 등록 완료: {}", member.getLoginId());
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