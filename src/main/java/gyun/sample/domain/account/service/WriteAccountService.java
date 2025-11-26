package gyun.sample.domain.account.service;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.request.AccountLoginRequest;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 계정 관련 쓰기 서비스
 * [리팩토링] ReadAccountService 상속 제거 -> Composition(주입)으로 변경
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WriteAccountService {

    // Read 기능을 주입받아 사용
    private final ReadAccountService readAccountService;

    // 로그인 시 필요한 데이터를 가져옵니다. (트랜잭션 내에서 수행해야 할 작업이 있다면 여기서 처리)
    public LoginMemberResponse getLoginData(CurrentAccountDTO request) {
        // ReadService를 통해 조회 및 활성 상태 검증
        Member member = readAccountService.findByLoginIdAndRole(request.loginId(), request.role());
        return new LoginMemberResponse(member);
    }

    @Deprecated(since = "Thymeleaf Project", forRemoval = true)
    public void validateLogin(AccountLoginRequest request) {
        log.info("Spring Security가 인증을 담당하므로 사용되지 않습니다.");
    }

    @Deprecated(since = "Thymeleaf Project", forRemoval = true)
    public void getJwtTokenByRefreshToken(String oldRefreshToken) {
        throw new GlobalException(ErrorCode.METHOD_NOT_SUPPORTED, "세션 기반 프로젝트에서는 지원하지 않습니다.");
    }
}