package gyun.sample.domain.member.repository;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.specification.MemberSpecification;
import gyun.sample.global.enums.GlobalActiveEnums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role);

    Optional<Member> findBySocialKeyAndRoleAndActiveAndMemberType(String socialKey, AccountRole role, GlobalActiveEnums active, MemberType memberType);

    boolean existsByLoginId(String loginId);

    boolean existsByNickName(String nickName);

    boolean existsByNickNameAndLoginIdNot(String nickName, String LoginId);

    boolean existsByRole(AccountRole role);

    Optional<Member> findByIdAndRoleIn(long id, List<AccountRole> accountRoles);

    Optional<Member> findByIdAndRole(long id, AccountRole accountRole);

    Optional<Member> findByLoginIdAndRoleIn(String loginId, List<AccountRole> accountRoles);

    /**
     * 회원 목록을 동적으로 조회합니다.
     * 서비스 계층의 호출을 유지하기 위해 default 메서드로 구현하고,
     * 내부적으로 Specification을 사용하여 동적 쿼리를 생성합니다.
     *
     * @param request  검색 조건 DTO
     * @param roles    조회할 권한 목록
     * @param pageable 페이징 정보
     * @return 페이징된 회원 목록
     */
    default Page<Member> getMemberList(MemberListRequestDTO request, List<AccountRole> roles, Pageable pageable) {
        Specification<Member> spec = MemberSpecification.getMemberListSpec(request, roles);
        return findAll(spec, pageable);
    }
}