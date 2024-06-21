package gyun.sample.domain.account.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.utils.AccountServiceUtil;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.domain.social.serviece.SocialService;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WriteAccountService extends AccountServiceUtil {

    public WriteAccountService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider, SocialServiceAdapter socialServiceAdapter) {
        super(memberRepository, refreshTokenRepository, jwtTokenProvider, socialServiceAdapter);
    }

    //    로그인
//        JWT 는 @InitBinder 에서 처리하기 적합하지 않아서 Service 에서 처리
    public AccountLoginResponse login(AccountLoginRequest request) {
        Member member = findByLoginIdAndRole(request.loginId(), request.role());
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, refreshToken);
    }

    //    리프레시 토큰으로 토큰 재발급
    public AccountLoginResponse getJwtTokenByRefreshToken(String oldRefreshToken) {
        final String loginId = jwtTokenProvider.deleteToken(oldRefreshToken);
        Member member = findByLoginId(loginId);
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, newRefreshToken);
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

    //    RefreshToken 제거
    @Transactional
    public boolean logout(CurrentAccountDTO currentAccountDTO) {
        refreshTokenRepository.deleteWithLoginId(currentAccountDTO.loginId());
        if (currentAccountDTO.memberType().checkSocialType()) {
            Member member = findByLoginId(currentAccountDTO.loginId());
            SocialService socialService = socialServiceAdapter.getService(member.getMemberType());
            socialService.logout(member.getSocialToken());
        }
        return true;
    }

}
