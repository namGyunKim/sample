package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;

public record InformationCustomerForAdminResponse(
        String loginId,             // 로그인 아이디
        String name                 // 이름
) {

    public InformationCustomerForAdminResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
