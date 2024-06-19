package gyun.sample.domain.member.payload.request.admin;

import gyun.sample.domain.member.enums.MemberOrderEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GetMemberListRequest(
        @Schema(description = "페이지 번호", example = "1")
        int page,
        @Schema(description = "페이지 사이즈", example = "10")
        int size,
        @NotNull(message = "정렬 기준을 선택해주세요.")
        MemberOrderEnums order,
        String searchWord) {
}
