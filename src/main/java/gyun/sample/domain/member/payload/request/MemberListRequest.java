package gyun.sample.domain.member.payload.request;

import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberListRequest(
        @Schema(description = "페이지 번호", example = "1")
        Integer page,

        @Schema(description = "페이지 사이즈", example = "10")
        Integer size,

        @Schema(description = "정렬 기준")
        GlobalOrderEnums order,

        @Schema(description = "검색어", example = "검색어")
        String searchWord,

        @Schema(description = "필터 기준")
        GlobalFilterEnums filter,

        @Schema(description = "활성화 여부")
        GlobalActiveEnums active
) {
    // 생성자에서 null 또는 유효하지 않은 값에 대한 기본값 설정
    public MemberListRequest {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        if (order == null) order = GlobalOrderEnums.CREATE_DESC; // 기본 정렬: 최신순
        if (searchWord == null) searchWord = "";
        if (filter == null) filter = GlobalFilterEnums.ALL;
        if (active == null) active = GlobalActiveEnums.ALL;
    }
}