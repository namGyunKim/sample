package gyun.sample.domain.member.payload.response.admin;

import gyun.sample.domain.member.entity.Member;

public record SaveMemberForAdminResponse(
        String loginId,             // 로그인 아이디
        String name                 // 이름
) {

    public SaveMemberForAdminResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
