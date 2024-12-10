package gyun.sample.domain.member.adapter;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.service.read.ReadMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadMemberServiceAdapter {


    private final ReadMemberService readUserService;
    private final ReadMemberService readAdminService;

    public ReadMemberService getService(AccountRole accountRole) {
        return switch (accountRole) {
            case USER -> readUserService;
            case SUPER_ADMIN, ADMIN -> readAdminService;
            default -> throw new IllegalArgumentException("지원하지 않는 권한 입니다: " + accountRole);
        };
    }
}