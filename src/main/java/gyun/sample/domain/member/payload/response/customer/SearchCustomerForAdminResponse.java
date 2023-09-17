package gyun.sample.domain.member.payload.response.customer;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;

public record SearchCustomerForAdminResponse(
        String loginId,             // 로그인 아이디
        String nickName,                 // 닉네임
        MemberType memberType,            // 회원 타입
        String createdAt               // 가입일
) {

    public SearchCustomerForAdminResponse(Member member) {

        this(
                member.getLoginId(),
                member.getNickName(),
                member.getMemberType(),
                member.getCreatedAt().toString());
    }
}
