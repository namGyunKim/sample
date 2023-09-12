package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.service.CustomerService;
import gyun.sample.domain.social.api.KakaoApiClient;
import gyun.sample.domain.social.api.KakaoAuthClient;
import gyun.sample.domain.social.payload.request.KakaoInfoRequest;
import gyun.sample.domain.social.payload.request.KakaoTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

// TODO: 2023/09/12 에러처리 및 개선 필요 기능만 넣어논 상태
@RequiredArgsConstructor
@Service
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class KakaoService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;

    @Value("${social.kakao.clientId}")
    private String clientId;
    @Value("${social.kakao.redirectUri}")
    private String redirectUri;
    @Value("${social.kakao.secretKey}")
    private String clientSecret;

    private final CustomerService customerService;

    //    code 받는 api 지만 redirect 문제가 있어서 get 방식으로 주소창에 쳐서 처리함
    public String getCode() {
        return kakaoAuthClient.getCode("code", clientId, redirectUri);
    }

    //    토큰 받는 api
    public KakaoTokenRequest getTokenByCode(String code) {
        return kakaoAuthClient.getToken("authorization_code", code, redirectUri, clientId, clientSecret);
    }


    @Transactional
    public AccountLoginResponse getInformationByToken(String accessToken) {
        KakaoInfoRequest request = kakaoApiClient.getInformation(accessToken);
        Map<String, Object> properties = request.getProperties();
        String nickName = (String) properties.get("nickname");
//        가입 여부 확인
        Optional<Member> member = customerService.findByIdAndActive(request.getId(), true);
        if (member.isEmpty()) {
//            가입되어 있지 않다면 회원가입
            Member saveMember = new Member(request.getId(), nickName, MemberType.KAKAO);
            customerService.saveMember(saveMember);
            return customerService.login(saveMember);
        } else {
            return customerService.login(member.get());
        }
    }


}
