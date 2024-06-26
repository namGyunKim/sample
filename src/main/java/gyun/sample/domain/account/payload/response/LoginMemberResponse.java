package gyun.sample.domain.account.payload.response;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginMemberResponse {
    private String loginId;         //로그인 아이디
    private String role;            //권한
    private String nickName;             //닉네임
    private String memberType;  //회원 타입
    private GlobalActiveEnums active;

    public LoginMemberResponse(Member member) {
        this.loginId = member.getLoginId();
        this.role = member.getRole().name();
        this.nickName = member.getNickName();
        this.memberType = member.getMemberType().name();
        this.active = member.getActive();
    }
}
