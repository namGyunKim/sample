package gyun.sample.domain.account.payload.response;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginMemberResponse {
    private Long id;                // 회원 ID (추가)
    private String loginId;         //로그인 아이디
    private String role;            //권한 (한글 명칭)
    private String nickName;        //닉네임
    private String memberType;      //회원 타입
    private GlobalActiveEnums active;

    public LoginMemberResponse(Member member) {
        this.id = member.getId(); // ID 매핑 추가
        this.loginId = member.getLoginId();
        this.role = member.getRole().getValue();
        this.nickName = member.getNickName();
        this.memberType = member.getMemberType().name();
        this.active = member.getActive();
    }
}