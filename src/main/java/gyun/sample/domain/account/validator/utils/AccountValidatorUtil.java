package gyun.sample.domain.account.validator.utils;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
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
    protected final MemberRepository userRepository;


    //    비밀번호 검사
    protected void passwordCheck(Member member, String password) {
        if (!BCrypt.checkpw(password, member.getPassword())) {
            throw new GlobalException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }

    //    로그인 아이디로 활성화 여부 관계없이 존재하는지 체크
    protected void existByLoginIdAndActiveAll(String loginId) {
        boolean isExist = userRepository.existByLoginIdAndActiveAll(loginId);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }

    //    로그인 아이디와 권한으로 존재하는지 체크
    protected void existLoginIdAndRole(String loginId, AccountRole role) {
        boolean isExist = userRepository.existByLoginIdAndRole(loginId, role);
        if (isExist) {
            throw new GlobalException(ErrorCode.ALREADY_REGISTERED_MEMBER_BY_LOGIN_ID);
        }
    }

    //    권한이 있는지 체크
    protected void checkRole(CurrentAccountDTO account, AccountRole role) {
        if (!account.role().name().toUpperCase().contains(role.name().toUpperCase())) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }
}
