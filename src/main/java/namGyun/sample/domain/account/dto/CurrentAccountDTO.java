package namGyun.sample.domain.account.dto;

import namGyun.sample.domain.account.enums.AccountRole;

public record CurrentAccountDTO(String loginId,
                                String name,
                                AccountRole role){

}
