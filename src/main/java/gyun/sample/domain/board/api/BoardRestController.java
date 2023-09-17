package gyun.sample.domain.board.api;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.payload.request.SaveBoardRequest;
import gyun.sample.domain.board.payload.response.SaveBoardResponse;
import gyun.sample.domain.board.service.BoardService;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BoardRestController", description = "게시판 api")
@RestController
@RequestMapping(value = "/api/board")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class BoardRestController {

    //    service
    private final BoardService boardService;
    //    utils
    protected final RestApiController restApiController;

    @PostMapping(value = "/save")
    @Operation(summary = "게시판 생성", description = "게시판 생성")
    public ResponseEntity<String> save(@Valid @RequestBody SaveBoardRequest request,
                                       @CurrentAccount CurrentAccountDTO account,
                                       BindingResult bindingResult) {
        SaveBoardResponse response = boardService.save(account,request);
        return restApiController.createSuccessRestResponse(response);
    }


}
