package gyun.sample.domain.board.service.read;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.board.payload.request.MyCommentRequestList;
import gyun.sample.domain.board.payload.response.MyCommentResponseList;
import gyun.sample.domain.board.repository.BoardCommentRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadCommentService {

    private final BoardCommentRepository boardCommentRepository;

    public BoardComment getCommentById(long id) {
        BoardComment comment = boardCommentRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_EXIST));
        commentValidate(comment);
        return comment;
    }

    public Page<MyCommentResponseList> myCommentList(CurrentAccountDTO currentAccountDTO, MyCommentRequestList requestList) {
        Pageable pageable = UtilService.getPageable(requestList.page(), requestList.size());
        GlobalActiveEnums active = requestList.active();
        if (active == GlobalActiveEnums.ALL) {
            Page<BoardComment> allByMemberId = boardCommentRepository.getAllByMemberId(currentAccountDTO.id(), pageable);
            return allByMemberId.map(MyCommentResponseList::new);
        } else {
            Page<BoardComment> allByMemberIdAndActive = boardCommentRepository.getAllByMemberIdAndActive(currentAccountDTO.id(), active, pageable);
            return allByMemberIdAndActive.map(MyCommentResponseList::new);
        }
    }

    public boolean isCommentOwner(long commentId, long memberId) {
        BoardComment comment = getCommentById(commentId);
        return comment.getMember().getId() == memberId;

    }


    private void commentValidate(BoardComment comment) {
        if (comment.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.COMMENT_INACTIVE);
        }
    }
}
