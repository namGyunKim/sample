package gyun.sample.domain.social.payload.request;

public record KakaoInfoRequest(

        String tokenType,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshTokenExpiresIn

) {
}
