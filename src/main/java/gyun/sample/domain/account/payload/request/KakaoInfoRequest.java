package gyun.sample.domain.account.payload.request;

public record KakaoInfoRequest(

        String tokenType,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshTokenExpiresIn

) {
}
