package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;

import java.util.List;

public abstract class AbstractWriteMemberService implements WriteMemberService {

    // 해당 서비스가 처리할 수 있는 권한 목록 반환
    public abstract List<AccountRole> getSupportedRoles();
}