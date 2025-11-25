package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.service.RedisService;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
public class WriteAccountService extends ReadAccountService {


    //    utils
    public final JwtTokenProvider jwtTokenProvider;
    //    service
    private final RedisService redisService;


    public WriteAccountService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        super(memberRepository);
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
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
        // Refresh Token도 재발급 (보안 및 사용성 개선)
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, newRefreshToken);
    }


    /**
     * 로그아웃: Refresh Token 제거 및 Access Token 블랙리스트 처리
     *
     * @param currentAccountDTO 현재 로그인 사용자 정보
     * @param accessToken       블랙리스트에 등록할 Access Token
     * @return 성공 여부
     */
    public boolean logout(CurrentAccountDTO currentAccountDTO, String accessToken) {
        Member member = findByLoginId(currentAccountDTO.loginId());

        // 1. Refresh Token 무효화 (DB에서 삭제)
        member.invalidateRefreshToken();

        // 2. Access Token 블랙리스트 등록 (Redis에 저장)
        Duration remainingTime = jwtTokenProvider.getRemainingTime(accessToken);

        if (remainingTime != null && !remainingTime.isNegative() && !remainingTime.isZero()) {
            // 남은 유효 기간 동안 Access Token을 블랙리스트에 저장
            redisService.set(accessToken, "logout", remainingTime);
        }
        return true;
    }

}