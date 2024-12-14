package gyun.sample.domain.member.payload.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;

public record MemberProfileListDTO(
        Long id,
        String loginId,
        String nickName,
        AccountRole role,
        MemberType memberType

) {

    public MemberProfileListDTO(Member member) {
        this(member.getId(), member.getLoginId(), member.getNickName(), member.getRole(), member.getMemberType());
    }
}
