package gyun.sample.domain.board.api;


import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.board.adapter.ReadBoardServiceAdapter;
import gyun.sample.domain.board.adapter.WriteBoardServiceAdapter;
import gyun.sample.domain.board.payload.request.*;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.domain.board.service.write.WriteBoardService;
import gyun.sample.domain.board.validator.BoardCreateValidator;
import gyun.sample.domain.board.validator.BoardInactiveValidator;
import gyun.sample.domain.board.validator.BoardListValidator;
import gyun.sample.domain.board.validator.BoardUpdateValidator;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BoardRestController", description = "게시판 api")
@RestController
@RequestMapping(value = "/api/board")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class BoardRestController {

    private final WriteBoardServiceAdapter writeBoardServiceAdapter;
    private final RestApiController restApiController;
    private final ReadBoardServiceAdapter readBoardServiceAdapter;
    //    validator 추가
    private final BoardCreateValidator boardCreateValidator;
    private final BoardUpdateValidator boardUpdateValidator;
    private final BoardListValidator boardListValidator;
    private final BoardInactiveValidator boardInactiveValidator;

    @InitBinder("boardCreateRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardCreateValidator);
    }

    @InitBinder("boardUpdateRequest")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardUpdateValidator);
    }

    @InitBinder("boardListRequest")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardListValidator);
    }

    @InitBinder("boardInactiveRequest")
    public void initBinder4(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardInactiveValidator);
    }

    @Operation(summary = "게시글 생성")
    @PostMapping(value = "/login/create")
    public ResponseEntity<String> createClan(@Valid @RequestBody BoardCreateRequest boardCreateRequest, BindingResult bindingResult,
                                             @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        WriteBoardService writeBoardService = writeBoardServiceAdapter.getService(boardCreateRequest.boardType());
        return restApiController.createSuccessRestResponse(writeBoardService.create(boardCreateRequest, currentAccountDTO));
    }

    @Operation(summary = "게시글 상세보기")
    @GetMapping(value = "/detail")
    public ResponseEntity<String> getBoard(@Valid BoardDetailRequest boardDetailRequest, BindingResult bindingResult) {
        ReadBoardService readBoardService = readBoardServiceAdapter.getService(boardDetailRequest.boardType());
        return restApiController.createSuccessRestResponse(readBoardService.getBoard(boardDetailRequest));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping(value = "/login/update")
    public ResponseEntity<String> updateBoard(@Valid @RequestBody BoardUpdateRequest boardUpdateRequest, BindingResult bindingResult) {
        WriteBoardService writeBoardService = writeBoardServiceAdapter.getService(boardUpdateRequest.boardType());
        return restApiController.createSuccessRestResponse(writeBoardService.update(boardUpdateRequest));
    }

    @Operation(summary = "게시글 리스트")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getBoardList(@Valid BoardListRequest boardListRequest, BindingResult bindingResult) {
        ReadBoardService readBoardService = readBoardServiceAdapter.getService(boardListRequest.boardType());
        return restApiController.createSuccessRestResponse(readBoardService.getBoardList(boardListRequest));
    }

    @Operation(summary = "게시글 비활성화")
    @PatchMapping(value = "/login/inactive")
    public ResponseEntity<String> inactiveBoard(@Valid @RequestBody BoardInactiveRequest boardInactiveRequest, BindingResult bindingResult, @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        WriteBoardService writeBoardService = writeBoardServiceAdapter.getService(boardInactiveRequest.boardType());
        return restApiController.createSuccessRestResponse(writeBoardService.inactive(boardInactiveRequest, currentAccountDTO));
    }

}
