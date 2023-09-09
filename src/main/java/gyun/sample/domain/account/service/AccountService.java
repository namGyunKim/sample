package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.utils.AccountServiceUtil;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccountService extends AccountServiceUtil {

    public AccountService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AccountValidator accountValidator, JwtTokenProvider jwtTokenProvider) {
        super(memberRepository, refreshTokenRepository, accountValidator, jwtTokenProvider);
    }

    //    로그인
    public AccountLoginResponse login(AccountLoginRequest request) {
        Member member = findByLoginIdAndRole(request.loginId(),request.role());
        accountValidator.login(member, request.password(),request.role());
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, refreshToken);
    }
//    리프레시 토큰으로 토큰 재발급
    public AccountLoginResponse getJwtTokenByRefreshToken(String oldRefreshToken) {
        Member member = findLoginIdByRefreshToken(oldRefreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        jwtTokenProvider.deleteToken(oldRefreshToken);
        return new AccountLoginResponse(accessToken, newRefreshToken);
    }


    // 인터셉터에서 터지는 JWT 토큰 에러
    public void jwtErrorException() {
        throw new JWTInterceptorException(ErrorCode.JWT_INVALID);
    }

    //    RefreshToken 제거
    public boolean logout(String refreshToken) {
        refreshTokenRepository.delete(refreshToken);
        return true;
    }

}
