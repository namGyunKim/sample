package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.payload.request.AllMemberRequest;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AllUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AllMemberRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        AllMemberRequest request = (AllMemberRequest) target;
        validateMemberRequest(request, errors);
    }

    private void validateMemberRequest(AllMemberRequest request, Errors errors) {
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
