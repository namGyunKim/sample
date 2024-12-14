package gyun.sample.domain.board.validator;

import gyun.sample.domain.board.payload.request.MyCommentListRequest;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class MyCommentListValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return MyCommentListRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MyCommentListRequest request = (MyCommentListRequest) target;
        validateRequest(request, errors);
    }

    private void validateRequest(MyCommentListRequest request, Errors errors) {


        if (!GlobalActiveEnums.checkComment(request.active()) && request.active() != null) {
            errors.rejectValue("active", "active.notComment", "댓글 상태가 아닙니다.");
        }

    }
}