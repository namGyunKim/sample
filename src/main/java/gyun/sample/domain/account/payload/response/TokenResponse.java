package gyun.sample.domain.account.payload.response;

public record TokenResponse(
        String loginId,         //로그인 아이디
        String role,            //권한
        String name             //이름
) {

}
