package gyun.sample.domain.board.validator;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.BoardCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardCreateValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return BoardCreateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        BoardCreateRequest request = (BoardCreateRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(BoardCreateRequest request, Errors errors) {
        if (request.boardType() == BoardType.ALL) {
            errors.rejectValue("boardType", "invalid.boardType", "게시판 타입을 선택해주세요.");
        }
    }
}
