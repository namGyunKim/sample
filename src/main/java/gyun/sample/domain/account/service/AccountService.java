package gyun.sample.domain.account.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.request.AccountLogoutRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    //    repository
    protected final MemberRepository memberRepository;
    protected final RefreshTokenRepository refreshTokenRepository;

    //    validator
    protected final AccountValidator accountValidator;

    //    utils
    protected final JwtTokenProvider jwtTokenProvider;

    public AccountLoginResponse login(AccountLoginRequest request) {
        Member member = findMemberByLoginIdAndRole(request.loginId(),request.role());
        accountValidator.validateLogin(member, request.password());
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, refreshToken);
    }

    public Member findMemberByLoginIdAndRole(String loginId, AccountRole role) {
        return memberRepository.findByLoginIdAndRole(loginId, role)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
    }

    public String findLoginIdByRefreshToken(String refreshToken) {
        String loginId = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (StringUtils.isEmpty(loginId)) {
            throw new GlobalException(ErrorCode.JWT_REFRESH_INVALID);
        }
        return loginId;
    }

    public Member findMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
    }

    public AccountLoginResponse getJwtTokenByRefreshToken(String oldRefreshToken) {
        String loginId = findLoginIdByRefreshToken(oldRefreshToken);
        Member member = findMemberByLoginId(loginId);
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        refreshTokenRepository.delete(oldRefreshToken);
        return new AccountLoginResponse(accessToken, newRefreshToken);
    }

    @Transactional
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public boolean existByRole(AccountRole role) {
        return memberRepository.existByRole(role);
    }

    public void jwtErrorException() {
        throw new JWTInterceptorException(ErrorCode.JWT_INVALID);
    }

//    RefreshToken 제거
    public boolean logout(AccountLogoutRequest request) {
        refreshTokenRepository.delete(request.refreshToken());
        return true;
    }
}
