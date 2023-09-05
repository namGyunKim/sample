package gyun.sample.global.utils;

import gyun.sample.domain.account.enums.TokenType;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;
    private final Date now = new Date();
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(Member member){
        Claims claims = Jwts.claims().setSubject(TokenType.ACCESS.name());
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        claims.put("loginId", member.getLoginId());
        claims.put("role", member.getRole());
        claims.put("name", member.getName());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Refresh 토큰 생성
     */
    public String createRefreshToken(Member member){
        Claims claims = Jwts.claims().setSubject(TokenType.REFRESH.name());
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        claims.put("loginId", member.getLoginId());
        claims.put("role", member.getRole());
        claims.put("name", member.getName());

        String refreshToken= Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

//         redis에 저장
//        redisTemplate.opsForValue().set(
//                TokenType.REFRESH.name(),
//                refreshToken,
//                refreshExpirationTime,
//                TimeUnit.MILLISECONDS
//        );

        refreshTokenRepository.save(refreshToken,member.getLoginId(),refreshExpirationTime);
        refreshTokenRepository.findByRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSRUZSRVNIIiwibG9naW5JZCI6InN1cGVyQWRtaW4iLCJyb2xlIjoiU1VQRVJfQURNSU4iLCJuYW1lIjoi7LWc6rOg6rSA66as7J6QIiwiaWF0IjoxNjkzODQ4NTM3LCJleHAiOjE2OTQ0NTMzMzd9.3AVwpE7bQtU29h9qjNQHShhdWeNXWnM2iERqjD282Gc");
        return refreshToken;
    }

    /**
     * Access 토큰을 검증
     */
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e) {
            throw new GlobalException(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch(JwtException e) {
            throw new GlobalException(ErrorCode.JWT_INVALID);
        }
    }

    /**
     * 토큰에서 회원 정보 추출
     */

    private Claims getTokenClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.info("유효하지 않은 토큰키");
            throw new GlobalException(ErrorCode.JWT_INVALID);
        }
        return claims;
    }

    public TokenResponse getTokenResponse(String token){
        Claims claims = getTokenClaims(token);
        return new TokenResponse(
                claims.get("loginId", String.class),
                claims.get("role", String.class),
                claims.get("name", String.class)
        );
    }

    public void deleteToken(String refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}