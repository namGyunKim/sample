package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.MemberRoleUpdateRequest;
import gyun.sample.global.security.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MemberRoleUpdateValidator implements Validator {

    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberRoleUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // 1. 현재 로그인한 사용자 정보(Principal) 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof PrincipalDetails principal)) {
            // 비정상적인 접근 (로그인이 안된 상태 등)
            errors.reject("auth.required", "로그인 정보가 유효하지 않습니다.");
            return;
        }

        // 2. URL Path Variable에서 변경 대상의 ID 추출 (/member/{role}/role-update/{id})
        Map<?, ?> pathVariables = (Map<?, ?>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String idStr = (String) pathVariables.get("id");

        Long targetId;
        try {
            targetId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            errors.reject("id.invalid", "유효하지 않은 회원 ID입니다.");
            return;
        }

        // 3. 검증 로직 실행

        // 3-1. 최고 관리자 권한 체크
        // (Controller의 @PreAuthorize가 1차적으로 막아주지만, 비즈니스 로직상 안전을 위해 이중 체크)
        if (principal.getRole() != AccountRole.SUPER_ADMIN) {
            errors.reject("auth.denied", "최고 관리자만 회원 등급을 변경할 수 있습니다.");
        }

        // 3-2. 본인 등급 변경 불가 체크
        if (principal.getId().equals(targetId)) {
            errors.reject("role.self", "자신의 등급은 변경할 수 없습니다.");
        }
    }
}