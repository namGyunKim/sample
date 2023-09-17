package gyun.sample.domain.board.service;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.request.SaveBoardRequest;
import gyun.sample.domain.board.payload.response.InformationBoardForAdminResponse;
import gyun.sample.domain.board.payload.response.SaveBoardResponse;
import gyun.sample.domain.board.repository.BoardRepository;
import gyun.sample.domain.board.service.utils.BoardServiceUtil;
import gyun.sample.domain.board.validator.BoardValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BoardService {

//    repository
    private final BoardRepository boardRepository;

//    validation
    private final BoardValidator boardValidator;

//    utils
    private final BoardServiceUtil boardServiceUtil;


    @Transactional
    public SaveBoardResponse save(CurrentAccountDTO account, SaveBoardRequest request) {
        boardValidator.save(request,account.role());
        Board saveBoard = boardServiceUtil.saveBoardProcess(request, account);
        boardRepository.save(saveBoard);
        return new SaveBoardResponse(saveBoard);
    }

    public InformationBoardForAdminResponse informationForAdminById(CurrentAccountDTO account, String id) {
        boardValidator.informationForAdminById(account);
        Board board = boardServiceUtil.findByIdForAdmin(id);
        return new InformationBoardForAdminResponse(board);
    }
}
