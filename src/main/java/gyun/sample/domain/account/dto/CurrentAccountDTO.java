package gyun.sample.domain.account.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;

public record CurrentAccountDTO(String loginId,         //로그인 아이디
                                String name,            //이름
                                AccountRole role        //권한
) {

    public CurrentAccountDTO(TokenResponse tokenResponse) {
        this(tokenResponse.loginId(), tokenResponse.name(), AccountRole.valueOf(tokenResponse.role()));
    }

}
