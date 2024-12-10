package gyun.sample.domain.member.adapter;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.service.write.WriteMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteMemberServiceAdapter {

    private final WriteMemberService writeUserService;
    private final WriteMemberService writeAdminService;

    public WriteMemberService getService(AccountRole accountRole) {
        return switch (accountRole) {
            case USER -> writeUserService;
            case SUPER_ADMIN, ADMIN -> writeAdminService;
            default -> throw new IllegalArgumentException("지원하지 않는 권한 타입입니다: " + accountRole);
        };
    }
}