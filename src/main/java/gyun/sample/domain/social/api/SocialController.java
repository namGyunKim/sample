package gyun.sample.domain.social.api;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.social.service.SocialServiceFactory;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.payload.response.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
@Tag(name = "SocialController", description = "소셜 로그인 API")
public class SocialController {

    private final SocialServiceFactory socialServiceFactory;
    private final RestApiController restApiController;

    /**
     * 구글 로그인 요청 URL 반환
     */
    @Operation(summary = "구글 로그인 리다이렉트 URL 요청", description = "프론트엔드에서 이 URL로 리다이렉트합니다.")
    @GetMapping("/google/login")
    public ResponseEntity<RestApiResponse<String>> googleLogin() {
        String url = socialServiceFactory.getService(MemberType.GOOGLE).getLoginRedirectUrl();
        return restApiController.createRestResponse(url);
    }

    /**
     * Google OAuth 2.0 인증 Code를 받아 JWT를 발급하는 Endpoint
     * 이 엔드포인트는 Google 설정의 redirectUri와 일치해야 합니다.
     * 성공 시 JWT 토큰을 응답 본문으로 반환합니다.
     */
    @Operation(summary = "구글 로그인 콜백", description = "Google 리다이렉션으로 호출되며 JWT를 발급하고 JSON 응답으로 반환합니다.")
    @GetMapping("/google/redirect")
    public ResponseEntity<RestApiResponse<AccountLoginResponse>> googleRedirect(@RequestParam String code) {

        AccountLoginResponse response = socialServiceFactory.getService(MemberType.GOOGLE).login(code);

        // 성공 시 JWT 토큰을 RestApiResponse에 담아 200 OK 응답으로 반환
        return restApiController.createRestResponse(response);
    }
}