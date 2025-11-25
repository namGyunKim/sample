package gyun.sample.domain.social.google.service;

import feign.FeignException;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.google.payload.response.GoogleTokenResponse;
import gyun.sample.domain.social.google.payload.response.GoogleUserInfoResponse;
import gyun.sample.domain.social.service.SocialLoginService;
import gyun.sample.global.config.social.GoogleApiClient;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class GoogleSocialService implements SocialLoginService {

    private final GoogleApiClient googleApiClient;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpClient httpClient;

    @Value("${social.google.baseUrl}")
    private String baseUrl;

    @Value("${social.google.clientId}")
    private String clientId;

    @Value("${social.google.redirectUri}")
    private String redirectUri;

    @Value("${social.google.secretKey}")
    private String clientSecret;

    @Value("${social.google.scope}")
    private String scope;

    // [수정] WriteUserService 제거
    public GoogleSocialService(GoogleApiClient googleApiClient, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.googleApiClient = googleApiClient;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @Override
    public MemberType getSupportedType() {
        return MemberType.GOOGLE;
    }

    @Override
    public String getLoginRedirectUrl() {
        // Google 로그인 페이지로 리다이렉트할 URL 생성
        try {
            return UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/o/oauth2/v2/auth")
                    .queryParam("client_id", clientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("response_type", "code")
                    .queryParam("scope", scope.replace(",", "%20"))
                    .toUriString();
        } catch (Exception e) {
            log.error("Google Redirect URL 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new SocialException(ErrorCode.GOOGLE_API_GET_CODE_ERROR, e);
        }
    }

    @Override
    public AccountLoginResponse login(String code) {
        // 1. Authorization Code로 Access Token 요청
        GoogleTokenResponse tokenResponse = getAccessToken(code);

        // 2. Access Token으로 사용자 정보 요청
        GoogleUserInfoResponse userInfo = getUserInfo(tokenResponse.accessToken());

        // 3. 회원가입 및 로그인 처리
        return registerOrLoginMember(userInfo, tokenResponse.accessToken());
    }

    /**
     * Google과의 연결을 끊고, Access Token을 무효화합니다. (탈퇴 시 사용)
     *
     * @param member Google 소셜 회원
     */
    public void unlink(Member member) {
        if (member.getMemberType() != MemberType.GOOGLE) {
            log.warn("Google 회원이 아님: {}", member.getLoginId());
            return;
        }

        String accessToken = member.getSocialToken();
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("Google Access Token이 없음. 연동 해제 불필요: {}", member.getLoginId());
            return;
        }

        try {
            // Google 토큰 취소 API URL: https://oauth2.googleapis.com/revoke?token={token}
            String revokeUrl = UriComponentsBuilder.fromUriString("https://oauth2.googleapis.com")
                    .path("/revoke")
                    .queryParam("token", accessToken)
                    .toUriString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(revokeUrl))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Google 토큰 취소 실패 (HTTP {}): {}", response.statusCode(), response.body());
                throw new SocialException(ErrorCode.GOOGLE_API_UNLINK_ERROR, "Google 토큰 취소 실패 (HTTP " + response.statusCode() + ") - " + response.body());
            }

            member.updateAccessToken(null);
            log.info("Google 토큰 취소 성공: {}", member.getLoginId());

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Google 토큰 취소 중 오류 발생: {}", e.getMessage(), e);
            throw new SocialException(ErrorCode.GOOGLE_API_UNLINK_ERROR, e);
        }
    }

    /**
     * 인증 코드로 Access Token을 요청하는 내부 메서드
     */
    private GoogleTokenResponse getAccessToken(String code) {
        try {
            return googleApiClient.getToken(
                    "authorization_code",
                    clientId,
                    clientSecret,
                    redirectUri,
                    code
            );
        } catch (FeignException e) {
            log.error("Google Access Token 요청 실패: {}", e.getMessage(), e);
            throw new SocialException(ErrorCode.GOOGLE_API_GET_TOKEN_ERROR, e);
        }
    }

    /**
     * Access Token으로 사용자 정보를 요청하는 내부 메서드
     */
    private GoogleUserInfoResponse getUserInfo(String accessToken) {
        try {
            return googleApiClient.getUserInfo(accessToken);
        } catch (FeignException e) {
            log.error("Google User Info 요청 실패: {}", e.getMessage(), e);
            throw new SocialException(ErrorCode.GOOGLE_API_GET_INFORMATION_ERROR, e);
        }
    }

    /**
     * 소셜 키(ID)를 기반으로 회원가입 또는 로그인 처리
     * 1. ACTIVE 회원 찾기 -> 로그인 및 토큰 업데이트
     * 2. INACTIVE 회원 찾기 -> ACTIVE로 전환 및 로그인
     * 3. 신규 회원 -> 회원가입
     */
    private AccountLoginResponse registerOrLoginMember(GoogleUserInfoResponse userInfo, String accessToken) {
        String socialKey = userInfo.id();
        MemberType memberType = MemberType.GOOGLE;
        AccountRole role = memberType.getDefaultRole();

        // 1. ACTIVE 회원 찾기 (일반 로그인)
        Optional<Member> activeMember = memberRepository.findBySocialKeyAndRoleAndActiveAndMemberType(
                socialKey, role, GlobalActiveEnums.ACTIVE, memberType
        );

        if (activeMember.isPresent()) {
            Member member = activeMember.get();
            log.info("Google 로그인 성공 (기존 ACTIVE 회원): {}", member.getLoginId());
            member.updateAccessToken(accessToken);
            String jwtAccessToken = jwtTokenProvider.createAccessToken(member);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(member);
            return new AccountLoginResponse(jwtAccessToken, jwtRefreshToken);
        }

        // 2. INACTIVE 회원 찾기 (탈퇴 후 재가입)
        Optional<Member> inactiveMember = memberRepository.findBySocialKeyAndRoleAndActiveAndMemberType(
                socialKey, role, GlobalActiveEnums.INACTIVE, memberType
        );

        if (inactiveMember.isPresent()) {
            Member member = inactiveMember.get();
            log.info("Google 재가입 성공 (기존 INACTIVE 회원, ACTIVE로 전환): {}", member.getLoginId());
            member.setActive(GlobalActiveEnums.ACTIVE); // ACTIVE로 전환
            member.updateAccessToken(accessToken); // 새 토큰 업데이트
            // Refresh Token도 재발급 (이전 토큰은 무효화되었을 가능성이 높음)
            String jwtAccessToken = jwtTokenProvider.createAccessToken(member);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(member);
            return new AccountLoginResponse(jwtAccessToken, jwtRefreshToken);
        }

        // 3. 신규 회원: 회원가입 및 로그인 처리
        log.info("Google 신규 회원가입 시작: SocialKey={}", socialKey);
        String nickName = userInfo.name() != null && !userInfo.name().isBlank() ? userInfo.name() : userInfo.email().split("@")[0];
        String loginId = memberType.name().toLowerCase() + "_" + socialKey;

        Member newMember = new Member(loginId, nickName, memberType, socialKey);
        newMember.updateAccessToken(accessToken);

        Member savedMember = memberRepository.save(newMember);

        String jwtAccessToken = jwtTokenProvider.createAccessToken(savedMember);
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(savedMember);
        return new AccountLoginResponse(jwtAccessToken, jwtRefreshToken);
    }
}