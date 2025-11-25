package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.JWTInterceptorException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
public class WriteAccountService extends ReadAccountService {


    public WriteAccountService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    //    로그인 (JWT 토큰 발급 로직 제거, 검증만 수행)
    //    실제 로그인 처리는 Spring Security의 formLogin이 담당하며, 이 메서드는 사용되지 않습니다.
    //    다만, 기존 Validator를 타기 위해 Controller에서는 사용자의 Role 검증이 필요합니다.
    @Deprecated(since = "Thymeleaf Project", forRemoval = true)
    public void validateLogin(AccountLoginRequest request) {
        // LoginAccountValidator에서 이미 비밀번호/활성화 검증을 했으므로, 여기서는 추가 작업이 없습니다.
        log.info("Thymeleaf 프로젝트에서는 이 메서드는 사용되지 않습니다. (Spring Security가 인증 담당)");
    }

    //    리프레시 토큰으로 토큰 재발급 로직 제거 (JWT 미사용)
    @Deprecated(since = "Thymeleaf Project", forRemoval = true)
    public void getJwtTokenByRefreshToken(String oldRefreshToken) {
        throw new GlobalException(ErrorCode.METHOD_NOT_SUPPORTED, "세션 기반 프로젝트에서는 Refresh Token 재발급 기능을 지원하지 않습니다.");
    }


    /**
     * 로그아웃: Refresh Token 제거 및 Access Token 블랙리스트 처리 로직 제거
     * 세션 기반에서는 Spring Security의 로그아웃 핸들러가 세션 무효화 처리를 담당합니다.
     * 여기서는 회원 엔티티의 RefreshToken만 무효화하는 로직을 남깁니다.
     *
     * @param currentAccountDTO 현재 로그인 사용자 정보
     * @param accessToken       (사용 안 함)
     * @return 성공 여부
     */
    public boolean logout(CurrentAccountDTO currentAccountDTO, String accessToken) {
        Member member = findByLoginId(currentAccountDTO.loginId());

        // 1. Refresh Token 무효화 (DB에서 삭제)
        member.invalidateRefreshToken();

        // 2. Access Token 블랙리스트 등록 (Redis에 저장) - JWT 미사용으로 로직 제거
        log.info("세션 기반 프로젝트로 전환: JWT 블랙리스트 처리 로직을 건너뜁니다.");
        // Duration remainingTime = jwtTokenProvider.getRemainingTime(accessToken);
        // if (remainingTime != null && !remainingTime.isNegative() && !remainingTime.isZero()) {
        //     redisService.set(accessToken, "logout", remainingTime);
        // }
        return true;
    }

    // JWT 에러 관련 메서드 제거
    @Deprecated(since = "Thymeleaf Project", forRemoval = true)
    public void jwtErrorException(String errorCode) {
        ErrorCode jwtErrorCode = ErrorCode.getByCode(errorCode);
        throw new JWTInterceptorException(jwtErrorCode);
    }
}