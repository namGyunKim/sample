package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;

public record SaveMemberResponse(
        String loginId,             // 로그인 아이디
        String name                 // 이름
) {

    public SaveMemberResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
