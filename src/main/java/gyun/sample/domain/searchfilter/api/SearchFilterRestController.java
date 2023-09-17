package gyun.sample.domain.searchfilter.api;


import gyun.sample.domain.board.enums.BoardType;
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
}
