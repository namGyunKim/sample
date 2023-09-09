package gyun.sample.domain.member.payload.response.customer;

import gyun.sample.domain.member.entity.Member;

public record SaveCustomerForSelfResponse(
        String loginId,             // 로그인 아이디
        String name                 // 이름
) {

    public SaveCustomerForSelfResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
