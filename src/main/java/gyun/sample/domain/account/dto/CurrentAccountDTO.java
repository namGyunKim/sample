package gyun.sample.domain.account.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;

public record CurrentAccountDTO(String loginId,
                                String name,
                                AccountRole role){

    public CurrentAccountDTO(TokenResponse tokenResponse) {
        this(tokenResponse.loginId(), tokenResponse.name(), AccountRole.valueOf(tokenResponse.role()));
    }

}
