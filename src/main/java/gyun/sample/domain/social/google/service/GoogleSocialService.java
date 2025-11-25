package gyun.sample.domain.social.google.service;

import feign.FeignException;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.write.WriteUserService;
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

@Slf4j
@Service
@Transactional
public class GoogleSocialService implements SocialLoginService {

    private final GoogleApiClient googleApiClient;
    private final MemberRepository memberRepository;
    private final WriteUserService writeUserService;
    private final JwtTokenProvider jwtTokenProvider;

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

    public GoogleSocialService(GoogleApiClient googleApiClient, MemberRepository memberRepository, WriteUserService writeUserService, JwtTokenProvider jwtTokenProvider) {
        this.googleApiClient = googleApiClient;
        this.memberRepository = memberRepository;
        this.writeUserService = writeUserService;
        this.jwtTokenProvider = jwtTokenProvider;
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
                    .queryParam("scope", scope.replace(",", "%20")) // 스코프는 공백으로 구분
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
            // Google의 User Info API는 GET 요청도 지원하지만, Feign Client는 Post 요청으로 구현했습니다.
            // GoogleApiClient.java의 주석을 확인하세요.
            return googleApiClient.getUserInfo(accessToken);
        } catch (FeignException e) {
            log.error("Google User Info 요청 실패: {}", e.getMessage(), e);
            throw new SocialException(ErrorCode.GOOGLE_API_GET_INFORMATION_ERROR, e);
        }
    }

    /**
     * 소셜 키(ID)를 기반으로 회원가입 또는 로그인 처리
     */
    private AccountLoginResponse registerOrLoginMember(GoogleUserInfoResponse userInfo, String accessToken) {
        // Google의 고유 ID를 Social Key로 사용
        String socialKey = userInfo.id();

        return memberRepository.findBySocialKeyAndRoleAndActiveAndMemberType(
                socialKey,
                MemberType.GOOGLE.getDefaultRole(),
                GlobalActiveEnums.ACTIVE,
                MemberType.GOOGLE
        ).map(member -> {
            // 1. 이미 존재하는 회원: 토큰 업데이트 및 로그인 처리
            log.info("Google 로그인 성공 (기존 회원): {}", member.getLoginId());
            member.updateAccessToken(accessToken); // 소셜 토큰 업데이트 (선택 사항)
            String jwtAccessToken = jwtTokenProvider.createAccessToken(member);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(member);
            return new AccountLoginResponse(jwtAccessToken, jwtRefreshToken);
        }).orElseGet(() -> {
            // 2. 신규 회원: 회원가입 및 로그인 처리
            log.info("Google 회원가입 시작: SocialKey={}", socialKey);
            // 닉네임은 Google name (null 방지를 위해 email도 고려)
            String nickName = userInfo.name() != null && !userInfo.name().isBlank() ? userInfo.name() : userInfo.email().split("@")[0];
            String loginId = MemberType.GOOGLE.name().toLowerCase() + "_" + socialKey;

            // 소셜 회원은 비밀번호 없이 생성
            Member newMember = new Member(loginId, nickName, MemberType.GOOGLE, socialKey);
            newMember.updateAccessToken(accessToken);

            // WriteUserService의 소셜 회원가입 로직이 있다면 사용 (현재는 일반 회원가입 로직만 있음)
            // 임시로 직접 save 후 JWT 생성
            Member savedMember = memberRepository.save(newMember);

            // JWT 생성
            String jwtAccessToken = jwtTokenProvider.createAccessToken(savedMember);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(savedMember);
            return new AccountLoginResponse(jwtAccessToken, jwtRefreshToken);
        });
    }
}