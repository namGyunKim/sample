package gyun.sample.domain.member.repository;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String >, MemberRepositoryCustom {

}
