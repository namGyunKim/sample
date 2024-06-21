package gyun.sample.domain.social;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.social.serviece.KakaoService;
import gyun.sample.domain.social.serviece.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialServiceAdapter {

    private final KakaoService kakaoService;
    // 다른 소셜 서비스들도 필요 시 추가
    // private final NaverService naverService;
    // private final GoogleService googleService;
    // private final FacebookService facebookService;
    // private final AppleService appleService;


    public SocialService<?, AccountLoginResponse, ?, AccountLoginResponse> getService(MemberType memberType) {
        return switch (memberType) {
            case KAKAO -> kakaoService;
            // 다른 소셜 서비스들도 필요 시 추가
            // case NAVER:
            //     return naverService;
            // case GOOGLE:
            //     return googleService;
            // case FACEBOOK:
            //     return facebookService;
            // case APPLE:
            //     return appleService;
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 타입입니다: " + memberType);
        };
    }
}