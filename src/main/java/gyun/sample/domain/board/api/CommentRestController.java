package gyun.sample.domain.board.api;


import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.board.payload.request.CommentCreateRequest;
import gyun.sample.domain.board.payload.request.CommentInactiveRequest;
import gyun.sample.domain.board.payload.request.MyCommentListRequest;
import gyun.sample.domain.board.service.read.ReadCommentService;
import gyun.sample.domain.board.service.write.WriteCommentService;
import gyun.sample.domain.board.validator.CommentCreateValidator;
import gyun.sample.domain.board.validator.InactiveCommentValidator;
import gyun.sample.domain.board.validator.MyCommentListValidator;
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

@Tag(name = "CommentRestController", description = "댓글 api")
@RestController
@RequestMapping(value = "/api/comment")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class CommentRestController {

    private final RestApiController restApiController;
    private final WriteCommentService writeCommentService;
    private final ReadCommentService readCommentService;

    //    validator 추가
    private final CommentCreateValidator commentCreateValidator;
    private final InactiveCommentValidator inactiveCommentValidator;
    private final MyCommentListValidator myCommentListValidator;

    @InitBinder("commentCreateRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(commentCreateValidator);
    }

    @InitBinder("commentInactiveRequest")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(inactiveCommentValidator);
    }

    @InitBinder("myCommentListRequest")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(myCommentListValidator);
    }

    @Operation(summary = "댓글 생성")
    @PostMapping("/login/create")
    public ResponseEntity<String> create(@RequestBody @Valid CommentCreateRequest commentCreateRequest, BindingResult bindingResult,
                                         @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        return restApiController.createSuccessRestResponse(writeCommentService.create(commentCreateRequest, currentAccountDTO));
    }

    @Operation(summary = "댓글 비활성화")
    @PatchMapping("/login/inactive")
    public ResponseEntity<String> inactive(@Valid @RequestBody CommentInactiveRequest commentInactiveRequest, BindingResult bindingResult,
                                           @CurrentAccount CurrentAccountDTO currentAccountDTO) {
        return restApiController.createSuccessRestResponse(writeCommentService.inactive(commentInactiveRequest, currentAccountDTO));
    }

    @Operation(summary = "내 댓글 목록")
    @GetMapping("/login/my")
    public ResponseEntity<String> myCommentList(@CurrentAccount CurrentAccountDTO currentAccountDTO, @Valid MyCommentListRequest myCommentListRequest, BindingResult bindingResult) {
        return restApiController.createSuccessRestResponse(readCommentService.myCommentList(currentAccountDTO, myCommentListRequest));
    }

}
