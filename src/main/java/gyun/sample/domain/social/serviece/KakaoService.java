package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.api.KakaoApiClient;
import gyun.sample.domain.social.api.KakaoAuthClient;
import gyun.sample.domain.social.payload.request.KakaoInfoRequest;
import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.SocialException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@RequiredArgsConstructor
@Transactional
public class KakaoService implements SocialService<KakaoTokenRequest, AccountLoginResponse, KakaoInfoRequest, AccountLoginResponse> {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Value("${social.kakao.clientId}")
    private String clientId;
    @Value("${social.kakao.redirectUri}")
    private String redirectUri;
    @Value("${social.kakao.secretKey}")
    private String clientSecret;

    //    토큰 받는 api
    @Override
    public KakaoTokenRequest getTokenByCode(String code) {
        try {
            return kakaoAuthClient.getToken("authorization_code", code, redirectUri, clientId, clientSecret);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.KAKAO_API_GET_TOKEN_ERROR);
        }
    }


    //    토큰으로 정보 가입 및 로그인 처리
    @Override
    public AccountLoginResponse createOrLoginByToken(String accessToken) {
        try {
            KakaoInfoRequest request = fetchKakaoInfo(accessToken);
            String uuid = generateUUID();
            String nickName = getNickName(request);
            Member member = createOrFetchMember(uuid, request, nickName, accessToken);
            return login(member);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.KAKAO_API_GET_INFORMATION_ERROR, e);
        }
    }

    private KakaoInfoRequest fetchKakaoInfo(String accessToken) {
        return kakaoApiClient.getInformation(accessToken);
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String getNickName(KakaoInfoRequest request) {
        Map<String, Object> properties = request.getProperties();
        return (String) properties.get("nickname");
    }

    public Member createOrFetchMember(String uuid, KakaoInfoRequest request, String nickName, String accessToken) {
        String loginId = uuid + MemberType.KAKAO + request.getId();
        String memberNickName = uuid + MemberType.KAKAO + nickName;
        return getWithSocial(
                loginId,
                AccountRole.USER,
                GlobalActiveEnums.ACTIVE,
                MemberType.KAKAO,
                memberNickName,
                accessToken,
                request.getId()
        );
    }

    //    토큰으로 로그아웃 처리
    @Override
    public KakaoInfoRequest logout(String accessToken) {
        try {
            return kakaoApiClient.logout("Bearer " + accessToken);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.KAKAO_API_LOGOUT_ERROR;
            throw new SocialException(errorCode, errorCode.getErrorMessage() + "     " + e.getMessage());
        }
    }

    //    토큰으로 회원탈퇴 처리
    @Override
    public KakaoInfoRequest unlink(String accessToken) {
        try {
            return kakaoApiClient.unlink("Bearer " + accessToken);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.KAKAO_API_UNLINK_ERROR;
            throw new SocialException(errorCode, errorCode.getErrorMessage() + "     " + e.getMessage());
        }
    }


    @Override
    public AccountLoginResponse login(String code) {
        KakaoTokenRequest tokenByCode = getTokenByCode(code);
        return createOrLoginByToken(tokenByCode.getAccessToken());
    }

    @Override
    public Member getWithSocial(String loginId, AccountRole accountRole, GlobalActiveEnums active, MemberType memberType, String nickName, String accessToken, String socialKey) {
        // 가입 여부 확인
        Member member = memberRepository.findBySocialKeyAndRoleAndActiveAndMemberType(
                socialKey, accountRole, active, memberType).orElseGet(() -> {
            // 회원이 존재하지 않을 경우 회원가입 처리
            Member newMember = new Member(loginId, nickName, memberType, socialKey);
            newMember.updateAccessToken(accessToken);
            return memberRepository.save(newMember);
        });
        member.updateAccessToken(accessToken);
        return member;
    }

    // 소셜 계정 로그인
    @Override
    public AccountLoginResponse login(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, refreshToken);
    }
}
