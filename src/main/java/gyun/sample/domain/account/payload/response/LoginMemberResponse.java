package gyun.sample.domain.account.payload.response;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginMemberResponse {
    private String loginId;         //로그인 아이디
    private String role;            //권한 (한글 명칭)
    private String nickName;        //닉네임
    private String memberType;      //회원 타입
    private GlobalActiveEnums active;

    public LoginMemberResponse(Member member) {
        this.loginId = member.getLoginId();
        // [수정] role.name() -> role.getValue()로 변경하여 한글 권한명("사용자", "관리자") 저장
        this.role = member.getRole().getValue();
        this.nickName = member.getNickName();
        this.memberType = member.getMemberType().name();
        this.active = member.getActive();
    }
}