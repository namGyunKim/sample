package gyun.sample.domain.board.service.write;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.entity.QuestionBoard;
import gyun.sample.domain.board.payload.request.CreateBoardRequest;
import gyun.sample.domain.board.payload.request.InactiveBoardRequest;
import gyun.sample.domain.board.payload.request.UpdateBoardRequest;
import gyun.sample.domain.board.repository.BoardRepository;
import gyun.sample.domain.board.repository.QuestionBoardRepository;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.domain.member.adapter.ReadMemberServiceAdapter;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import gyun.sample.global.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WriteQuestionBoardService implements WriteBoardService {

    private final ReadMemberServiceAdapter readMemberServiceAdapter;
    private final BoardRepository boardRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final ReadBoardService readQuestionBoardService;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
    public GlobalCreateResponse create(CreateBoardRequest request, CurrentAccountDTO currentAccountDTO) {
        Member member = getMember(currentAccountDTO);
        String createIp = UtilService.getClientIp(httpServletRequest);
        Board board = new Board(request, member, createIp);
        QuestionBoard questionBoard = new QuestionBoard(board);

        questionBoardRepository.save(questionBoard); // 먼저 QuestionBoard를 저장합니다.
        board.addQuestion(questionBoard);

        Board savedBoard = boardRepository.save(board); // 그 후 Board를 저장합니다.

        member.addBoard(savedBoard);
        return new GlobalCreateResponse(savedBoard.getId());
    }

    @Override
    @Transactional
    public GlobalUpdateResponse update(UpdateBoardRequest request) {
        Board board = readQuestionBoardService.getBoardById(request.boardId());
        board.update(request);
        return new GlobalUpdateResponse(board.getId());
    }

    @Override
    @Transactional
    public GlobalInactiveResponse inactive(InactiveBoardRequest inactiveBoardRequest, CurrentAccountDTO currentAccountDTO) {
        Board board = readQuestionBoardService.getBoardById(inactiveBoardRequest.boardId());
        Member member = getMember(currentAccountDTO);
        String inactiveIp = UtilService.getClientIp(httpServletRequest);
        board.inactive(inactiveBoardRequest.inactiveReason(), member, inactiveIp);
        return new GlobalInactiveResponse(board.getId());
    }

    public Member getMember(CurrentAccountDTO currentAccountDTO) {
        ReadMemberService readMemberService = readMemberServiceAdapter.getService(currentAccountDTO.role());
        return readMemberService.getByLoginIdAndRole(currentAccountDTO.loginId(), currentAccountDTO.role());
    }
}
