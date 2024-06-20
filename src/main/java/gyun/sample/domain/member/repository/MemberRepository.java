package gyun.sample.domain.member.repository;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginId(String loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginIdAndRoleAndActive(String loginId, AccountRole role, boolean active);

    boolean existsByLoginId(String loginId);

    boolean existsByNickName(String nickName);

    boolean existsByRole(AccountRole role);

    Optional<Member> findByIdAndRoleInAndActive(long id, List<AccountRole> accountRoles, boolean active);
}
