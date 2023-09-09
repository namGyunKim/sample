package gyun.sample.domain.member.payload.response.customer;

import gyun.sample.domain.member.entity.Member;

public record UpdateCustomerForSelfResponse(
        String loginId,             // 로그인 아이디
        String name                 // 이름
) {

    public UpdateCustomerForSelfResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
