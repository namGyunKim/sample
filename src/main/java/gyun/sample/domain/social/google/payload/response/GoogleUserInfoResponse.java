package gyun.sample.domain.social.google.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Google User Info API 응답 레코드 (https://www.googleapis.com/oauth2/v2/userinfo)
 */
public record GoogleUserInfoResponse(
        @JsonProperty("id")
        String id,                  // 구글 고유 ID (Social Key로 사용)
        @JsonProperty("email")
        String email,               // 이메일
        @JsonProperty("verified_email")
        Boolean verifiedEmail,      // 이메일 인증 여부
        @JsonProperty("name")
        String name,                // 전체 이름
        @JsonProperty("given_name")
        String givenName,           // 이름
        @JsonProperty("family_name")
        String familyName,          // 성
        @JsonProperty("picture")
        String picture,             // 프로필 이미지 URL
        @JsonProperty("locale")
        String locale               // 언어/지역
) {
}