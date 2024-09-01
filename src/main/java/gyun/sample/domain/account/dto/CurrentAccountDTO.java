package gyun.sample.domain.account.dto;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.enums.MemberType;

public record CurrentAccountDTO(
        Long id,                        //회원 아이디
        String loginId,         //로그인 아이디
        String nickName,            //닉네임
        AccountRole role,        //권한
        MemberType memberType   //회원 타입
) {

    public CurrentAccountDTO(TokenResponse tokenResponse) {
        this(tokenResponse.id(), tokenResponse.loginId(), tokenResponse.nickName(), AccountRole.valueOf(tokenResponse.role()), MemberType.valueOf(tokenResponse.memberType()));
    }

    public static CurrentAccountDTO generatedGuest() {
        return new CurrentAccountDTO(0L, "GUEST", "GUEST", AccountRole.GUEST, MemberType.GENERAL);
    }

}
