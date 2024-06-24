package gyun.sample.domain.account.service;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadAccountService {
    protected final MemberRepository memberRepository;


    public LoginMemberResponse getLoginData(CurrentAccountDTO request) {
        Member byLoginIdAndRole = memberRepository.findByLoginIdAndRole(request.loginId(), request.role()).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (byLoginIdAndRole.getActive() != GlobalActiveEnums.ACTIVE)
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        return new LoginMemberResponse(byLoginIdAndRole);
    }

    //    로그인 아이디와 권한으로 멤버 조회 및 활성 여부 검증
    public Member findByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));

        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        }
        return member;
    }

    //    로그인 아이디로 멤버 조회 및 활성 여부 검증
    public Member findByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));

        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        }
        return member;
    }

    // 인터셉터에서 터지는 JWT 토큰 에러
    public void jwtErrorException(String errorCode) {
        ErrorCode jwtErrorCode = ErrorCode.getByCode(errorCode);
        throw new JWTInterceptorException(jwtErrorCode);
    }

    // 인터셉터에서 터지는  에러
    public void AccessException(String errorMessage) {
        ErrorCode accessException = ErrorCode.ACCESS_DENIED;
        throw new GlobalException(accessException, errorMessage);
    }
}
