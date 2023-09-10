package gyun.sample.domain.account.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.social.payload.response.KakaoTokenResponse;

public record CurrentAccountDTO(String loginId,         //로그인 아이디
                                String name,            //이름
                                AccountRole role        //권한
) {

    public CurrentAccountDTO(KakaoTokenResponse kakaoTokenResponse) {
        this(kakaoTokenResponse.loginId(), kakaoTokenResponse.name(), AccountRole.valueOf(kakaoTokenResponse.role()));
    }

}
