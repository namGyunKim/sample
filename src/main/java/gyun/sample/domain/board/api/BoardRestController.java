package gyun.sample.domain.board.api;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.adapter.ReadBoardServiceAdapter;
import gyun.sample.domain.board.adapter.WriteBoardServiceAdapter;
import gyun.sample.domain.board.payload.request.*;
import gyun.sample.domain.board.service.read.ReadBoardService;
import gyun.sample.domain.board.service.write.WriteBoardService;
import gyun.sample.domain.board.validator.BoardValidatorList;
import gyun.sample.domain.board.validator.CreateBoardValidator;
import gyun.sample.domain.board.validator.InactiveBoardValidator;
import gyun.sample.domain.board.validator.UpdateBoardValidator;
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
    private final CreateBoardValidator createBoardValidator;
    private final UpdateBoardValidator updateBoardValidator;
    private final BoardValidatorList boardValidatorList;
    private final InactiveBoardValidator inactiveBoardValidator;

    @InitBinder("createBoardRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createBoardValidator);
    }

    @InitBinder("updateBoardRequest")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(updateBoardValidator);
    }

    @InitBinder("boardRequestList")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardValidatorList);
    }

    @InitBinder("inactiveBoardRequest")
    public void initBinder4(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(inactiveBoardValidator);
    }

    @Operation(summary = "게시글 생성")
    @PostMapping(value = "/login/create")
    public ResponseEntity<String> createClan(@Valid @RequestBody CreateBoardRequest createBoardRequest, BindingResult bindingResult,
                                             @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        WriteBoardService writeBoardService = writeBoardServiceAdapter.getService(createBoardRequest.boardType());
        return restApiController.createSuccessRestResponse(writeBoardService.create(createBoardRequest, currentAccountDTO));
    }

    @Operation(summary = "게시글 상세보기")
    @GetMapping(value = "/detail")
    public ResponseEntity<String> getBoard(@Valid DetailBoardRequest detailBoardRequest, BindingResult bindingResult) {
        ReadBoardService readBoardService = readBoardServiceAdapter.getService(detailBoardRequest.boardType());
        return restApiController.createSuccessRestResponse(readBoardService.getBoard(detailBoardRequest));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping(value = "/login/update")
    public ResponseEntity<String> updateBoard(@Valid @RequestBody UpdateBoardRequest updateBoardRequest, BindingResult bindingResult) {
        WriteBoardService writeBoardService = writeBoardServiceAdapter.getService(updateBoardRequest.boardType());
        return restApiController.createSuccessRestResponse(writeBoardService.update(updateBoardRequest));
    }

    @Operation(summary = "게시글 리스트")
    @GetMapping(value = "/list")
    public ResponseEntity<String> getBoardList(@Valid BoardRequestList boardRequestList, BindingResult bindingResult) {
        ReadBoardService readBoardService = readBoardServiceAdapter.getService(boardRequestList.boardType());
        return restApiController.createSuccessRestResponse(readBoardService.getBoardList(boardRequestList));
    }

    @Operation(summary = "게시글 비활성화")
    @PatchMapping(value = "/login/inactive")
    public ResponseEntity<String> inactiveBoard(@Valid @RequestBody InactiveBoardRequest inactiveBoardRequest, BindingResult bindingResult, @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        WriteBoardService writeBoardService = writeBoardServiceAdapter.getService(inactiveBoardRequest.boardType());
        return restApiController.createSuccessRestResponse(writeBoardService.inactive(inactiveBoardRequest, currentAccountDTO));
    }

}
