package gyun.sample.domain.member.payload.response.admin;

import gyun.sample.domain.member.entity.Member;

public record InformationCustomerForAdminResponse(
        String loginId,             // 로그인 아이디
        String nickName                 // 닉네임
) {

    public InformationCustomerForAdminResponse(Member member) {
        this(member.getLoginId(), member.getNickName());
    }
}
