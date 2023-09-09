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
public interface MemberRepository extends JpaRepository<Member, String >, MemberRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginId(String loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role);
    boolean existsByLoginId(String loginId);

    //    권한으로 활성화 된 유저 존재여부 확인
    boolean existsByRole(AccountRole role);
}
