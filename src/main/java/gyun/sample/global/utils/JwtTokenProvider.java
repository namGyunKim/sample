package gyun.sample.global.utils;

import gyun.sample.domain.account.enums.TokenType;
import gyun.sample.domain.account.payload.dto.ClaimsWithErrorCodeDTO;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

// JWT 토큰 생성 및 검증 유틸
@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JwtTokenProvider {


    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    private final MemberRepository memberRepository;

    // SecretKey 객체 생성 메소드
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(Member member) {
        Date now = new Date();
        Date expireDate = getExpireDate(TokenType.ACCESS);

        return Jwts.builder()
                .subject(TokenType.ACCESS.name())
                .claim("id", member.getId())
                .claim("loginId", member.getLoginId())
                .claim("role", member.getRole())
                .claim("nickName", member.getNickName())
                .claim("memberType", member.getMemberType())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Refresh 토큰 생성
     */
    @Transactional
    public String createRefreshToken(Member member) {
        Date now = new Date();
        Date expireDate = getExpireDate(TokenType.REFRESH);

        String refreshToken = Jwts.builder()
                .subject(TokenType.REFRESH.name())
                .claim("id", member.getId())
                .claim("loginId", member.getLoginId())
                .claim("role", member.getRole())
                .claim("nickName", member.getNickName())
                .claim("memberType", member.getMemberType())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();

        member.updateRefreshToken(refreshToken);
        return refreshToken;
    }

    private Date getExpireDate(TokenType tokenType) {
        Date now = new Date();
        final long expirationTime = tokenType == TokenType.ACCESS ? accessExpirationTime : refreshExpirationTime;
        return new Date(now.getTime() + expirationTime); // 이미 properties에서 밀리초 단위라면 * 1000 불필요, 확인 필요 (설정파일엔 3600000 등으로 되어있어 그대로 사용)
    }

    /**
     * 토큰에서 회원 정보 추출
     */
    private ClaimsWithErrorCodeDTO getTokenClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new ClaimsWithErrorCodeDTO(claims, null);
        } catch (ExpiredJwtException e) {
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_EXPIRED);
        } catch (io.jsonwebtoken.security.SignatureException e) { // 패키지 명시
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_SIGNATURE_ERROR);
        } catch (JwtException e) {
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_INVALID);
        } catch (Exception e) {
            log.error("JWT 토큰 에러 {}", e.getMessage(), e);
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_UNKNOWN_ERROR);
        }
    }


    // 토큰에서 회원 정보 추출
    public TokenResponse getTokenResponse(String token) {
        ClaimsWithErrorCodeDTO claimsWithErrorCodeDTO = getTokenClaims(token);
        Claims claims = claimsWithErrorCodeDTO.claims();

        // Claims 객체가 null인 경우 처리
        if (claims == null) {
            return TokenResponse.generatedGuest(claimsWithErrorCodeDTO.errorCode());
        }

        return new TokenResponse(
                claims.get("id", Long.class),
                claims.get("loginId", String.class),
                claims.get("role", String.class),
                claims.get("nickName", String.class),
                claims.get("memberType", String.class),
                claimsWithErrorCodeDTO.errorCode()
        );
    }


    public TokenResponse getTokenResponse(HttpServletRequest httpServletRequest) {
        try {
            String bearer = httpServletRequest.getHeader("Authorization").split(" ")[1];
            return getTokenResponse(bearer);
        } catch (Exception e) {
            return TokenResponse.generatedGuest(ErrorCode.JWT_INVALID);
        }
    }

    /**
     * Request Header에서 "Bearer "를 제거한 Access Token 문자열을 추출합니다.
     *
     * @param request HttpServletRequest
     * @return Access Token 문자열, 없으면 null
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰의 남은 유효 기간(Duration)을 반환합니다.
     *
     * @param token Access Token
     * @return 남은 유효 기간 (Duration), 만료되었거나 유효하지 않으면 null
     */
    public Duration getRemainingTime(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            long expirationTimeMillis = claims.getExpiration().getTime();
            long nowMillis = new Date().getTime();

            if (expirationTimeMillis > nowMillis) {
                return Duration.ofMillis(expirationTimeMillis - nowMillis);
            }
        } catch (ExpiredJwtException e) {
            // 이미 만료된 토큰 (남은 시간 0으로 처리하거나 null 처리)
            return Duration.ZERO;
        } catch (JwtException e) {
            // 기타 JWT 에러 (서명 오류 등)
            log.warn("Failed to get remaining time for token: {}", e.getMessage());
        }
        return null;
    }
}