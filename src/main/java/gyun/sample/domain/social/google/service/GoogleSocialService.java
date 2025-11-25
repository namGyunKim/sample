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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // [수정] JwtTokenProvider 제거
    public GoogleSocialService(GoogleApiClient googleApiClient, MemberRepository memberRepository) {
        this.googleApiClient = googleApiClient;
        this.memberRepository = memberRepository;
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

    /**
     * [변경] 소셜 코드를 이용해 회원 정보(Member 객체)를 가져오는 메서드
     */
    public Member getMemberBySocialCode(String code) {
        // 1. Authorization Code로 Access Token 요청
        GoogleTokenResponse tokenResponse = getAccessToken(code);

        // 2. Access Token으로 사용자 정보 요청
        GoogleUserInfoResponse userInfo = getUserInfo(tokenResponse.accessToken());

        // 3. 회원가입 및 로그인 처리 후 Member 객체 반환
        return registerOrLoginMember(userInfo, tokenResponse.accessToken());
    }

    @Override
    @Deprecated(since = "Thymeleaf Project", forRemoval = true)
    // SocialLoginService 인터페이스를 구현하기 위해 남김. SocialController에서 Member 객체를 얻기 위해 사용됨.
    public AccountLoginResponse login(String code) {
        // JWT가 필요 없으므로, Member 객체를 찾은 후 임시 응답 반환
        Member member = getMemberBySocialCode(code);
        // SocialController가 세션을 만들 수 있도록 Member 정보를 담아 반환
        return new AccountLoginResponse(member.getLoginId(), member.getNickName());
    }

    /**
     * [제거] JWT 토큰에서 loginId 추출하는 임시 메서드 제거
     */
    public String extractLoginIdFromToken(String accessToken) {
        // 이 메서드는 이제 사용되지 않습니다.
        throw new UnsupportedOperationException("세션 기반 프로젝트에서는 JWT 토큰 추출 로직을 사용하지 않습니다.");
    }


    /**
     * Google과의 연결을 끊고, Access Token을 무효화합니다. (탈퇴 시 사용)
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

        // ... (토큰 취소 로직은 기존과 동일)
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
     * 소셜 키(ID)를 기반으로 회원가입 또는 로그인 처리 후 Member 객체 반환
     */
    private Member registerOrLoginMember(GoogleUserInfoResponse userInfo, String accessToken) {
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
            return member;
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
            return member;
        }

        // 3. 신규 회원: 회원가입 및 로그인 처리
        log.info("Google 신규 회원가입 시작: SocialKey={}", socialKey);
        String nickName = userInfo.name() != null && !userInfo.name().isBlank() ? userInfo.name() : userInfo.email().split("@")[0];
        String loginId = memberType.name().toLowerCase() + "_" + socialKey;

        // 비밀번호 필드가 NOT NULL이면 문제 발생: 소셜 회원은 임시 비밀번호 설정
        // Member 엔티티의 생성자를 수정하거나, 비밀번호가 없는 경우를 처리해야 합니다.
        Member newMember = new Member(loginId, nickName, memberType, socialKey);
        // [수정] Member 엔티티의 updatePassword 메서드를 사용하여 암호화된 임시 비밀번호를 설정해야 합니다.
        // 현재 Member 엔티티 생성자는 password를 받지 않고, WriteUserService::createMember에서 인코딩합니다.
        // 여기서는 임시 비밀번호를 생성하여 저장해야 합니다.
        // **주의: 소셜 회원은 비밀번호가 null이어야 할 수도 있습니다. Member 엔티티의 `password` 필드가 NotNull이 아님을 확인했습니다.**
        // 다만, Spring Security의 UserDetails 구현체는 getPassword()가 NotNull을 요구하므로, 임시값을 설정합니다.
        newMember.updatePassword(new BCryptPasswordEncoder().encode("social_temp_password_" + socialKey)); // 임시 비밀번호 암호화 저장
        newMember.updateAccessToken(accessToken);

        Member savedMember = memberRepository.save(newMember);
        return savedMember;
    }
}