package gyun.sample.domain.member.payload.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.global.utils.UtilService;

// 상세 조회용 DTO에 감사(Audit) 정보를 추가합니다.
public record MemberProfileDetailDTO(
        Long id,
        String loginId,
        String nickName,
        AccountRole role,
        MemberType memberType,
        String createdAt,       // 생성일 포맷팅 문자열
        String createdBy,       // 생성자
        String modifiedAt,      // 수정일 포맷팅 문자열
        String modifiedBy       // 수정자
) {

    public MemberProfileDetailDTO(Member member) {
        this(
                member.getId(),
                member.getLoginId(),
                member.getNickName(),
                member.getRole(),
                member.getMemberType(),
                UtilService.formattedTime(member.getCreatedAt()),
                member.getCreatedBy(),
                UtilService.formattedTime(member.getModifiedAt()),
                member.getLastModifiedBy()
        );
    }
}