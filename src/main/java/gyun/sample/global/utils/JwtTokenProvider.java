package gyun.sample.global.utils;

import gyun.sample.domain.account.enums.TokenType;
import gyun.sample.domain.account.payload.dto.ClaimsWithErrorCodeDTO;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.enums.ErrorCode;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(Member member) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(TokenType.ACCESS.name());
        Date expireDate = getExpireDate(TokenType.ACCESS);

        claims.put("id", member.getId());
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
    @Transactional
    public String createRefreshToken(Member member) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(TokenType.REFRESH.name());
        Date expireDate = getExpireDate(TokenType.REFRESH);

        claims.put("id", member.getId());
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

        member.updateRefreshToken(refreshToken);
        return refreshToken;
    }

    private Date getExpireDate(TokenType tokenType) {
        Date now = new Date();
        final long expirationTime = tokenType == TokenType.ACCESS ? accessExpirationTime : refreshExpirationTime;
        return new Date(now.getTime() + expirationTime * 1000 * 60 * 60);
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
            return new ClaimsWithErrorCodeDTO(null, ErrorCode.JWT_EXPIRED);
        } catch (SignatureException e) {
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
}
