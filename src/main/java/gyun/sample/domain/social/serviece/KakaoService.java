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
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@Transactional(readOnly = true)
public class KakaoService extends BaseSocialService implements SocialService<KakaoTokenRequest, AccountLoginResponse, KakaoInfoRequest, AccountLoginResponse> {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;

    public KakaoService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, KakaoAuthClient kakaoAuthClient, KakaoApiClient kakaoApiClient) {
        super(memberRepository, jwtTokenProvider);
        this.kakaoAuthClient = kakaoAuthClient;
        this.kakaoApiClient = kakaoApiClient;
    }

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
            KakaoInfoRequest request = kakaoApiClient.getInformation(accessToken);
            Map<String, Object> properties = request.getProperties();
            String nickName = (String) properties.get("nickname");
            Member member = super.getWithSocial("kakao" + request.getId(), AccountRole.USER, GlobalActiveEnums.ACTIVE, MemberType.KAKAO, nickName, accessToken);
            return super.login(member);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.KAKAO_API_GET_INFORMATION_ERROR, e);
        }
    }

    //    토큰으로 로그아웃 처리
    @Override
    public KakaoInfoRequest logout(String accessToken) {
        try {
            return kakaoApiClient.logout("Bearer " + accessToken);
        } catch (Exception e) {
//            throw new GlobalException(ErrorCode.KAKAO_API_LOGOUT_ERROR, e);
            return null;
        }
    }

    //    토큰으로 회원탈퇴 처리
    @Override
    public KakaoInfoRequest unlink(String accessToken) {
        try {
            return kakaoApiClient.unlink("Bearer " + accessToken);
        } catch (Exception e) {
//            throw new GlobalException(ErrorCode.KAKAO_API_UNLINK_ERROR,e);
        }
        return null;
    }


    @Override
    public AccountLoginResponse login(String code) {
        KakaoTokenRequest tokenByCode = getTokenByCode(code);
        return createOrLoginByToken(tokenByCode.getAccessToken());
    }
}
