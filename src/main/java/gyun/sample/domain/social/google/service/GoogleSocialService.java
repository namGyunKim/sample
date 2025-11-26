package gyun.sample.domain.social.google.service;

import feign.FeignException;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.log.enums.LogType;
import gyun.sample.domain.log.event.MemberActivityEvent;
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
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final ApplicationEventPublisher eventPublisher;
    private final HttpServletRequest httpServletRequest;
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

    public GoogleSocialService(GoogleApiClient googleApiClient, MemberRepository memberRepository, ApplicationEventPublisher eventPublisher, HttpServletRequest httpServletRequest) {
        this.googleApiClient = googleApiClient;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
        this.httpServletRequest = httpServletRequest;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    @Override
    public MemberType getSupportedType() {
        return MemberType.GOOGLE;
    }

    @Override
    public String getLoginRedirectUrl() {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope.replace(",", "%20"))
                .toUriString();
    }

    public Member getMemberBySocialCode(String code) {
        GoogleTokenResponse tokenResponse = getAccessToken(code);
        GoogleUserInfoResponse userInfo = getUserInfo(tokenResponse.accessToken());
        return registerOrLoginMember(userInfo, tokenResponse.accessToken());
    }

    @Override
    public AccountLoginResponse login(String code) {
        Member member = getMemberBySocialCode(code);
        return new AccountLoginResponse(member.getLoginId(), member.getNickName());
    }

    // 구글 연동 해제
    public void unlink(Member member) {
        if (member.getMemberType() != MemberType.GOOGLE || member.getSocialToken() == null) return;
        try {
            String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + member.getSocialToken();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(revokeUrl)).POST(HttpRequest.BodyPublishers.noBody()).build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            member.updateAccessToken(null);
        } catch (Exception e) {
            log.error("Google unlink failed", e);
        }
    }

    private GoogleTokenResponse getAccessToken(String code) {
        try {
            return googleApiClient.getToken("authorization_code", clientId, clientSecret, redirectUri, code);
        } catch (FeignException e) {
            throw new SocialException(ErrorCode.GOOGLE_API_GET_TOKEN_ERROR, e);
        }
    }

    private GoogleUserInfoResponse getUserInfo(String accessToken) {
        try {
            return googleApiClient.getUserInfo(accessToken);
        } catch (FeignException e) {
            throw new SocialException(ErrorCode.GOOGLE_API_GET_INFORMATION_ERROR, e);
        }
    }

    private Member registerOrLoginMember(GoogleUserInfoResponse userInfo, String accessToken) {
        String socialKey = userInfo.id();

        // 1. "활동 중(ACTIVE)"인 회원만 조회
        // 탈퇴한 회원은 ID가 변경되었고 상태가 INACTIVE이므로 조회되지 않음 -> 신규 가입 로직으로 넘어감
        Optional<Member> activeMember = memberRepository.findBySocialKeyAndRoleAndActiveAndMemberType(
                socialKey, AccountRole.USER, GlobalActiveEnums.ACTIVE, MemberType.GOOGLE
        );

        if (activeMember.isPresent()) {
            Member member = activeMember.get();
            member.updateAccessToken(accessToken);
            // 로그인 로그
            eventPublisher.publishEvent(MemberActivityEvent.of(member.getLoginId(), member.getId(), member.getLoginId(), LogType.LOGIN, "GOOGLE 로그인", UtilService.getClientIp(httpServletRequest)));
            return member;
        }

        // 2. 신규 회원 가입 (탈퇴 후 재가입 포함)
        String loginId = "google_" + socialKey;

        // 닉네임 중복 체크 및 처리 로직 추가
        String originalNickName = userInfo.name();
        String nickName = originalNickName;

        // 닉네임이 이미 존재하면 랜덤 숫자를 붙여서 유니크하게 만듦
        while (memberRepository.existsByNickName(nickName)) {
            nickName = originalNickName + "_" + (int)(Math.random() * 10000);
        }

        Member newMember = new Member(loginId, nickName, MemberType.GOOGLE, socialKey);
        newMember.updatePassword(new BCryptPasswordEncoder().encode("SOCIAL_" + socialKey));
        newMember.updateAccessToken(accessToken);

        Member savedMember = memberRepository.save(newMember);

        // 가입 로그
        eventPublisher.publishEvent(MemberActivityEvent.of(savedMember.getLoginId(), savedMember.getId(), savedMember.getLoginId(), LogType.JOIN, "GOOGLE 회원가입", UtilService.getClientIp(httpServletRequest)));

        return savedMember;
    }
}