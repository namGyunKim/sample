package gyun.sample.domain.account.dto;

import gyun.sample.domain.account.enums.AccountRole;

public record CurrentAccountDTO(String loginId,
                                String name,
                                AccountRole role){

}
