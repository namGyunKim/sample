package gyun.sample.domain.member.repository;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.global.enums.GlobalActiveEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    Optional<Member> findByPhoneNumberAndCountryCodeAndActive(String phoneNumber, String countryCode, GlobalActiveEnums active);

    boolean existsByLoginIdAndPhoneNumberAndCountryCodeAndActive(String loginId, String phoneNumber, String countryCode, GlobalActiveEnums active);

    Optional<Member> findByIdAndActive(long id, GlobalActiveEnums active);

    Optional<Member> findByRefreshTokenAndActive(String refreshToken, GlobalActiveEnums active);

}
