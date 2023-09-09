package gyun.sample.domain.account.validator.utils;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountValidatorUtil {
    protected final MemberRepository memberRepository;


    //    비밀번호 검사
    protected void passwordCheck(Member member, String password) {
        if (!BCrypt.checkpw(password, member.getPassword())) {
            throw new GlobalException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }

    //    로그인 아이디로 활성화 여부 관계없이 존재하는지 체크
    protected void notExistByLoginId(String loginId) {
        boolean isExist = memberRepository.existsByLoginId(loginId);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }

//    권한이 있는지 검사

    protected void checkRole(AccountRole myRole, AccountRole requiredRole) {


        boolean checkRole = myRole.equals(requiredRole);
//        관리자 권한이 필요할 경우 슈퍼관리자도 가능하게
        if (requiredRole.equals(AccountRole.ADMIN) && myRole.equals(AccountRole.SUPER_ADMIN)) {
            checkRole = true;
        }

        if (!requiredRole.equals(AccountRole.ALL) && !checkRole) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

//    조회한 멤버의 권한이 타겟 권한과 같은지 검사
    protected void checkTargetRole(Member member,AccountRole targetRole) {
        if (!member.getRole().equals(targetRole)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }
}
