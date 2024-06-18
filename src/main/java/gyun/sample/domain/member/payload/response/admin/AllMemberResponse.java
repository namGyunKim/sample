package gyun.sample.domain.member.payload.response.admin;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AllMemberResponse {
    private Long id;
    private String loginId;
    private String nickName;
    private AccountRole role;
    private MemberType memberType;

    public AllMemberResponse (Member member){
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.nickName = member.getNickName();
        this.role = member.getRole();
        this.memberType = member.getMemberType();
    }
}