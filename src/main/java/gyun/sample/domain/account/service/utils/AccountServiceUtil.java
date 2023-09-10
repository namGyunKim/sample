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
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;


@RequiredArgsConstructor
public class AccountServiceUtil {

    //    repository
    protected final MemberRepository memberRepository;
    protected final RefreshTokenRepository refreshTokenRepository;

    //    validator
    protected final AccountValidator accountValidator;

    //    utils
    protected final JwtTokenProvider jwtTokenProvider;

    //    리프레시 토큰으로 로그인 아이디 조회
    public Member findLoginIdByRefreshToken(String refreshToken) {
        String loginId = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (StringUtils.isEmpty(loginId)) {
            throw new GlobalException(ErrorCode.JWT_REFRESH_INVALID);
        }
        return findByLoginId(loginId);
    }

    //    멤버 저장
    @Transactional
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    //    해당 권한의 계정이 존재하는지 체크
    public boolean existsByRole(AccountRole role) {
        return memberRepository.existsByRole(role);
    }

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
        Member member = memberRepository.findByLoginIdAndRole(loginId, role)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));

        System.out.println("member = " + member.isActive());

        if (!member.isActive()) {
            throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        }
        return member;
    }

    //    회원 탈퇴
    @Transactional
    public void deactivateMember(String loginId) {
        Member member = findByLoginId(loginId);
        member.deactivate();
    }
}
