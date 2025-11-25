package gyun.sample.domain.social.google.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Google OAuth 2.0 Access Token 응답 레코드
 */
public record GoogleTokenResponse(
        @JsonProperty("access_token")
        String accessToken,         // 액세스 토큰
        @JsonProperty("expires_in")
        Integer expiresIn,          // 액세스 토큰 유효 기간 (초)
        @JsonProperty("scope")
        String scope,               // 토큰 권한 범위
        @JsonProperty("token_type")
        String tokenType,           // 토큰 타입 (Bearer)
        @JsonProperty("id_token")
        String idToken              // ID 토큰 (사용자 기본 정보 포함, 선택 사항)
) {
}