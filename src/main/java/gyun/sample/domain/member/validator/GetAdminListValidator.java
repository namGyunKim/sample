package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GetAdminListValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return GetMemberListRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        GetMemberListRequest request = (GetMemberListRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(GetMemberListRequest request, Errors errors) {
        if (!GlobalOrderEnums.checkAdminMember(request.order())) {
            errors.rejectValue("order", "order.notAdmin", "관리자 정렬 기준이 아닙니다.");
        }

        if (!GlobalFilterEnums.checkAdminMember(request.filter())) {
            errors.rejectValue("filter", "filter.notAdmin", "관리자 필터 기준이 아닙니다.");
        }
    }
}
