package gyun.sample.domain.account.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;

public record CurrentAccountDTO(String loginId,         //로그인 아이디
                                String nickName,            //닉네임
                                AccountRole role        //권한
) {

    public CurrentAccountDTO(TokenResponse tokenResponse) {
        this(tokenResponse.loginId(), tokenResponse.nickName(), AccountRole.valueOf(tokenResponse.role()));
    }

}
