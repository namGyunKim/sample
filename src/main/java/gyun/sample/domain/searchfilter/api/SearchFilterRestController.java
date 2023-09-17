package gyun.sample.domain.searchfilter.api;


import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.searchfilter.enums.ActiveType;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SearchFilterRestController", description = "검색 필터 모음 api")
@RestController
@RequestMapping(value = "/api/search/filter")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class SearchFilterRestController {

    //    utils
    private final RestApiController restApiController;

    @GetMapping(value = "/board-type")
    @Operation(summary = "게시판 타입", description = "게시판 타입")
    public ResponseEntity<String> boardType() {
        return restApiController.createSuccessRestResponse(BoardType.toStrings());
    }

    @GetMapping(value = "/member-type")
    @Operation(summary = "회원 타입", description = "회원 타입")
    public ResponseEntity<String> memberType() {
        return restApiController.createSuccessRestResponse(MemberType.toStrings());
    }

    @GetMapping(value = "/active-type")
    @Operation(summary = "활성화 타입", description = "활성화 타입")
    public ResponseEntity<String> activeType() {
        return restApiController.createSuccessRestResponse(ActiveType.toStrings());
    }
}
