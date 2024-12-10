package gyun.sample.domain.board.validator;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.CreateBoardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CreateBoardValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return CreateBoardRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        CreateBoardRequest request = (CreateBoardRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(CreateBoardRequest request, Errors errors) {
        if (request.boardType() == BoardType.ALL) {
            errors.rejectValue("boardType", "invalid.boardType", "게시판 타입을 선택해주세요.");
        }
    }
}
