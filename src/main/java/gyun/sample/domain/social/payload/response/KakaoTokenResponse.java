package gyun.sample.domain.social.payload.response;

public record KakaoTokenResponse(
        String loginId,         //로그인 아이디
        String role,            //권한
        String name             //이름
) {

}
