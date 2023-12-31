package gyun.sample.domain.member.payload.response.customer;

import gyun.sample.domain.member.entity.Member;

public record InformationCustomerForSelfResponse(
        String loginId,             // 로그인 아이디
        String nickName                 // 닉네임
) {

    public InformationCustomerForSelfResponse(Member member) {
        this(member.getLoginId(), member.getNickName());
    }
}
