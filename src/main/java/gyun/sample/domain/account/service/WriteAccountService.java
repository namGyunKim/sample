package gyun.sample.domain.account.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.domain.social.serviece.SocialService;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WriteAccountService extends ReadAccountService {

    public final RefreshTokenRepository refreshTokenRepository;

    //    utils
    public final JwtTokenProvider jwtTokenProvider;

    public final SocialServiceAdapter socialServiceAdapter;

    public WriteAccountService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider, SocialServiceAdapter socialServiceAdapter) {
        super(memberRepository);
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.socialServiceAdapter = socialServiceAdapter;
    }

    //    로그인
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


    //    RefreshToken 제거
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
