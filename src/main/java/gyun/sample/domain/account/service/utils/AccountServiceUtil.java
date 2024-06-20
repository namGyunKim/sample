package gyun.sample.domain.account.service.utils;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AccountServiceUtil {

    //    repository
    protected final MemberRepository memberRepository;
    protected final RefreshTokenRepository refreshTokenRepository;

    //    validator
    protected final AccountValidator accountValidator;

    //    utils
    protected final JwtTokenProvider jwtTokenProvider;


    //    로그인 아이디로 멤버 조회 및 활성 여부 검증
    public Member findByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));

        if (!member.isActive()) {
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        }
        return member;
    }

    //    로그인 아이디와 권한으로 멤버 조회 및 활성 여부 검증
    public Member findByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRoleAndActive(loginId, role,true)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));

        if (!member.isActive()) {
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        }
        return member;
    }
}
