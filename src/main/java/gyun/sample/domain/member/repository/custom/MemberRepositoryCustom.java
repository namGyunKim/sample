package gyun.sample.domain.member.repository.custom;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    //    로그인 아이디로 활성화 및 비활성화 된 유저 존재여부 확인
    boolean existByLoginIdAndActiveAll(String loginId);
    //    로그인 아이디와 권환으로 활성화 된 유저 존재여부 확인
    boolean existByLoginIdAndRole(String loginId, AccountRole role);
    //    권한으로 활성화 된 유저 존재여부 확인
    boolean existByRole(AccountRole role);
    //    로그인 아이디와 권한으로 활성화 된 유저 찾기
    Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role);
    //    로그인 아이디로 활성화 된 유저 찾기
    Optional<Member> findByLoginId(String loginId);
}
