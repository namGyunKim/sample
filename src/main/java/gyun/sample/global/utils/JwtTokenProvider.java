package gyun.sample.global.utils;

import gyun.sample.domain.account.dto.ClaimsWithErrorCodeDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.enums.TokenType;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.global.error.enums.ErrorCode;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

// JWT 토큰 생성 및 검증 유틸
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(Member member) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(TokenType.ACCESS.name());
        Date expireDate = new Date(now.getTime() + accessExpirationTime * 1000);

        claims.put("loginId", member.getLoginId());
        claims.put("role", member.getRole());
        claims.put("nickName", member.getNickName());
        claims.put("memberType", member.getMemberType());
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
    public String createRefreshToken(Member member) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(TokenType.REFRESH.name());
        Date expireDate = new Date(now.getTime() + refreshExpirationTime * 1000);

        claims.put("loginId", member.getLoginId());
        claims.put("role", member.getRole());
        claims.put("nickName", member.getNickName());
        claims.put("memberType", member.getMemberType());

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        refreshTokenRepository.save(refreshToken, member.getLoginId(), refreshExpirationTime);
        return refreshToken;
    }

    /**
     * 토큰에서 회원 정보 추출
     */
    private ClaimsWithErrorCodeDTO getTokenClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return new ClaimsWithErrorCodeDTO(claims, null);
        } catch (ExpiredJwtException e) {
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_SIGNATURE_ERROR);
        } catch (JwtException e) {
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_INVALID);
        } catch (Exception e) {
            log.error("JWT 토큰 에러 {}", e.getMessage(),e);
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_UNKNOWN_ERROR);
        }
    }


    // 토큰에서 회원 정보 추출
    public TokenResponse getTokenResponse(String token) {
        ClaimsWithErrorCodeDTO claimsWithErrorCodeDTO = getTokenClaims(token);
        Claims claims = claimsWithErrorCodeDTO.claims();

        // Claims 객체가 null인 경우 처리
        if (claims == null) {
            return new TokenResponse("guest", AccountRole.GUEST.name(), "guest", MemberType.GUEST.name(), claimsWithErrorCodeDTO.errorCode());
        }

        return new TokenResponse(
                claims.get("loginId", String.class),
                claims.get("role", String.class),
                claims.get("nickName", String.class),
                claims.get("memberType", String.class),
                claimsWithErrorCodeDTO.errorCode()
        );
    }

    // 리프레쉬 토큰 제거
    public void deleteToken(String refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
