package gyun.sample.domain.account.validator.utils;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import io.micrometer.common.util.StringUtils;
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

    //   관리자 이상의 권한이 있는지 검사
    protected void checkAdminRole(AccountRole role) {
        if (!(role == AccountRole.ADMIN || role == AccountRole.SUPER_ADMIN)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    //   슈퍼 관리자 권한이 있는지 검사
    protected void checkSuperAdminRole(AccountRole role) {
        if (role != AccountRole.SUPER_ADMIN) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    //   고객 권한이 있는지 검사
    protected void checkCustomerRole(AccountRole role) {
        if (role != AccountRole.CUSTOMER) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

//    비밀번호 유효성 체크
    protected void passwordValidation(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$";
        if (!password.matches(regex) && !StringUtils.isBlank(password)) {
            throw new GlobalException(ErrorCode.PASSWORD_INVALID);
        }
    }
}
