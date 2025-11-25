package gyun.sample.global.security.guard;

import gyun.sample.domain.account.enums.AccountRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberGuard {

    /**
     * 회원 생성/수정 권한 체크
     * 관리자(ADMIN, SUPER_ADMIN)이거나, 요청된 역할이 USER인 경우 허용
     */
    public boolean checkCreateRole(AccountRole role) {
        // 역할이 USER라면 누구나(로그인한 사용자) 생성 가능 (혹은 비로그인도 가능하게 하려면 SecurityConfig 설정 필요)
        if (role == AccountRole.USER) {
            return true;
        }

        // 그 외의 역할(ADMIN 등)을 생성하려면 현재 사용자가 관리자 권한이어야 함
        // 단, @PreAuthorize 내부에서는 현재 인증된 사용자의 권한(Authentication)을 자동으로 참조하지 않으므로
        // hasAnyRole과 조합해서 쓰거나 여기서 SecurityContext를 조회해야 합니다.
        // 여기서는 단순히 role 값 자체 검증 로직만 수행하고,
        // 호출 측에서 hasAnyRole() || @memberGuard.check(...) 형태로 조합하는 것이 좋습니다.

        return false;
    }
}