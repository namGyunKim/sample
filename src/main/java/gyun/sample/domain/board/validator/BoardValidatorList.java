package gyun.sample.domain.board.validator;

import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.BoardRequestList;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardValidatorList implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return BoardRequestList.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        BoardRequestList request = (BoardRequestList) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(BoardRequestList request, Errors errors) {
        if (request.boardType() == BoardType.ALL) {
            errors.rejectValue("boardType", "invalid.boardType", "게시판 타입을 선택해주세요.");
        }


        if (!GlobalOrderEnums.checkBoard(request.order()) && request.order() != null) {
            errors.rejectValue("order", "order.notBoard", "게시판 정렬 기준이 아닙니다.");
        }

        if (!GlobalFilterEnums.checkBoard(request.filter()) && request.filter() != null) {
            errors.rejectValue("filter", "filter.notBoard", "게시판 필터 기준이 아닙니다.");
        }

        if (!GlobalActiveEnums.checkBoard(request.active()) && request.active() != null) {
            errors.rejectValue("active", "active.notBoard", "게시판 상태가 아닙니다.");
        }

    }
}
