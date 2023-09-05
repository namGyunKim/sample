package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AccountValidator accountValidator;
    private final RefreshTokenRepository refreshTokenRepository;

    public AccountLoginResponse login(AccountLoginRequest request) {
        Member member = findMemberByLoginIdAndRole(request);
        accountValidator.validateLogin(member, request.password());
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, refreshToken);
    }

    public Member findMemberByLoginIdAndRole(AccountLoginRequest request) {
        return memberRepository.findByLoginIdAndRole(request.loginId(), request.role())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
    }

    public String findLoginIdByRefreshToken(String refreshToken){
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
}
