package gyun.sample.domain.board.service.write;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.adapter.ReadBoardServiceAdapter;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.board.payload.request.CreateCommentRequest;
import gyun.sample.domain.board.payload.request.InactiveCommentRequest;
import gyun.sample.domain.board.repository.BoardCommentRepository;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.domain.board.service.read.ReadCommentService;
import gyun.sample.domain.member.adapter.ReadMemberServiceAdapter;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WriteCommentService {

    private final ReadMemberServiceAdapter readMemberServiceAdapter;
    private final ReadBoardServiceAdapter readBoardServiceAdapter;
    private final ReadCommentService readCommentService;
    private final BoardCommentRepository commentRepository;
    private final HttpServletRequest httpServletRequest;

    @Transactional
    public GlobalCreateResponse create(CreateCommentRequest request, CurrentAccountDTO currentAccountDTO) {
        Board board = getBoard(request);
        Member member = getMember(currentAccountDTO);
        BoardComment parentComment = getParentComment(request);

        String createIp = UtilService.getClientIp(httpServletRequest);

        BoardComment comment = new BoardComment(request, board, member, parentComment, createIp);
        commentRepository.save(comment);

        board.addComment(comment);
        return new GlobalCreateResponse(comment.getId());
    }

    @Transactional
    public GlobalInactiveResponse inactive(InactiveCommentRequest request, CurrentAccountDTO currentAccountDTO) {
        BoardComment comment = readCommentService.getCommentById(request.commentId());
        Member member = getMember(currentAccountDTO);
        String inactiveIp = UtilService.getClientIp(httpServletRequest);
        comment.deActive(request, member, inactiveIp);
        return new GlobalInactiveResponse(comment.getId());
    }

    private Board getBoard(CreateCommentRequest request) {
        ReadBoardService readBoardService = readBoardServiceAdapter.getService(request.boardType());
        return readBoardService.getBoardById(request.boardId());
    }

    private Member getMember(CurrentAccountDTO currentAccountDTO) {
        ReadMemberService readMemberService = readMemberServiceAdapter.getService(currentAccountDTO.role());
        return readMemberService.getByLoginIdAndRole(currentAccountDTO.loginId(), currentAccountDTO.role());
    }

    private BoardComment getParentComment(CreateCommentRequest request) {
        if (request.parentId() >= 1) {
            return readCommentService.getCommentById(request.parentId());
        }
        return null;
    }

}
