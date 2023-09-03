package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;

public record SaveMemberResponse(String loginId, String name) {

    public SaveMemberResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
