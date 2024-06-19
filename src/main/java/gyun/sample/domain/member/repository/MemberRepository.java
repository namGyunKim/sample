package gyun.sample.domain.member.repository;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long >, MemberRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginId(String loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role);
    boolean existsByLoginId(String loginId);

    boolean existsByNickName(String nickName);

    boolean existsByRole(AccountRole role);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginIdAndActive(String loginId, boolean active);
}
