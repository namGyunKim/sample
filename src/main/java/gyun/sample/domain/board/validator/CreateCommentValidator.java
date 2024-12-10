package gyun.sample.domain.board.validator;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.CreateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CreateCommentValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return CreateCommentRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        CreateCommentRequest request = (CreateCommentRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(CreateCommentRequest request, Errors errors) {
        if (request.boardType() == BoardType.ALL) {
            errors.rejectValue("boardType", "invalid.boardType", "게시판 타입을 선택해주세요.");
        }
    }
}
