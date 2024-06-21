package gyun.sample.domain.member.repository;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role);

    boolean existsByLoginId(String loginId);

    boolean existsByNickName(String nickName);

    boolean existsByNickNameAndLoginIdNot(String nickName, String LoginId);

    boolean existsByRole(AccountRole role);

    Optional<Member> findByIdAndRoleIn(long id, List<AccountRole> accountRoles);
    Optional<Member> findByIdAndRole(long id, AccountRole accountRole);

    Optional<Member> findByLoginIdAndRoleIn(String loginId, List<AccountRole> accountRoles);
}
