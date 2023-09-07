package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;

public record InformationCustomerForAdminResponse(String loginId, String name) {

    public InformationCustomerForAdminResponse(Member member) {
        this(member.getLoginId(), member.getName());
    }
}
