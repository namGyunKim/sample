package gyun.sample.domain.board.service.utils;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.payload.request.SaveBoardRequest;
import gyun.sample.domain.board.repository.BoardRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@AllArgsConstructor
public class BoardServiceUtil {

//    service
    private final AccountService accountService;
//    repository
    private final BoardRepository boardRepository;

//    게시판 생성 로직
    @Transactional
    public Board saveBoardProcess(SaveBoardRequest request, CurrentAccountDTO account){
        if (account.role().equals(AccountRole.GUEST)){
            return new Board(request);
        }else{
            Member member = accountService.findByLoginIdAndActive(account.loginId(),true).orElseThrow( () -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
            return new Board(request, member);
        }
    }

//    관리자를 위한 게시글 조회
    public Board findByIdForAdmin(String id){
        return boardRepository.findById(id).orElseThrow( () -> new GlobalException(ErrorCode.NOT_EXIST_BOARD));
    }
}
