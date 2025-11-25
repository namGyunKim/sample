package gyun.sample.domain.account.payload.response;

// [수정] JWT 대신 로그인 ID와 닉네임을 반환하도록 변경 (소셜 로그인 처리 호환용)
public record AccountLoginResponse(
        String loginId,         //로그인 아이디
        String nickName         //닉네임
) {
}