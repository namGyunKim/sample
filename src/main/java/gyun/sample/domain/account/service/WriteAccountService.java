package gyun.sample.domain.account.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.domain.social.serviece.SocialService;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WriteAccountService extends ReadAccountService {


    //    utils
    public final JwtTokenProvider jwtTokenProvider;

    public final SocialServiceAdapter socialServiceAdapter;


    public WriteAccountService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, SocialServiceAdapter socialServiceAdapter) {
        super(memberRepository);
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
        Member member = memberRepository.findByRefreshTokenAndActive(oldRefreshToken, GlobalActiveEnums.ACTIVE).orElseThrow(() -> new GlobalException(ErrorCode.JWT_REFRESH_INVALID));
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, newRefreshToken);
    }


    //    RefreshToken 제거
    public boolean logout(CurrentAccountDTO currentAccountDTO) {
        Member member = findByLoginId(currentAccountDTO.loginId());
        member.invalidateRefreshToken();
        if (currentAccountDTO.memberType().checkSocialType()) {
            SocialService socialService = socialServiceAdapter.getService(member.getMemberType());
            socialService.logout(member.getSocialToken());
        }
        return true;
    }

}
