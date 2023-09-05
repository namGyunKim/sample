package gyun.sample.domain.member.repository.custom;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    boolean existByLoginIdAndActiveAll(String loginId);

    boolean existByRole(AccountRole role);

    Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role);

    Optional<Member> findByLoginId(String loginId);
}
