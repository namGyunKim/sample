package gyun.sample.domain.member.payload.request;

import gyun.sample.domain.account.enums.AccountRole;
import jakarta.validation.constraints.NotNull;

public record MemberRoleUpdateRequest(
        @NotNull(message = "변경할 권한을 선택해주세요.")
        AccountRole role
) {
}