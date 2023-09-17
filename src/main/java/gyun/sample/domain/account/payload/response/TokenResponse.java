package gyun.sample.domain.account.payload.response;

public record TokenResponse(
        String loginId,         //로그인 아이디
        String role,            //권한
        String nickName,             //닉네임
        String memberType  //회원 타입
) {

}
