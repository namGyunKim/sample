package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.MemberListRequest;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MemberListValidator implements Validator {

    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberListRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberListRequest request = (MemberListRequest) target;

        // URL Path Variable에서 Role 추출
        Map<?, ?> pathVariables = (Map<?, ?>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String roleStr = (String) pathVariables.get("role");
        AccountRole role = AccountRole.create(roleStr);

        // 일반 사용자(USER) 조회 시 관리자 전용 필터/정렬 사용 불가 체크
        if (role == AccountRole.USER) {
            if (!GlobalOrderEnums.checkAdminMember(request.order()) && request.order() != null) {
                errors.rejectValue("order", "order.notAdmin", "관리자 정렬 기준이 아닙니다.");
            }

            if (!GlobalFilterEnums.checkAdminMember(request.filter()) && request.filter() != null) {
                errors.rejectValue("filter", "filter.notAdmin", "관리자 필터 기준이 아닙니다.");
            }

            if (!GlobalActiveEnums.checkMember(request.active()) && request.active() != null) {
                errors.rejectValue("active", "active.notMember", "회원 상태가 아닙니다.");
            }
        }
    }
}