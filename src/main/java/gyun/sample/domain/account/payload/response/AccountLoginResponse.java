package gyun.sample.domain.account.payload.response;

public record AccountLoginResponse(
        String accessToken,         //엑세스 토큰
        String refreshToken         //리프레쉬 토큰
) {
}
