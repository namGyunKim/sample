package gyun.sample.global.security.filter;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.security.PrincipalDetailsService;
import gyun.sample.global.service.RedisService;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalDetailsService principalDetailsService;
    private final RedisService redisService; // 블랙리스트 확인을 위한 RedisService 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 2. 토큰이 존재할 경우 유효성 검사 및 블랙리스트 확인
        if (token != null) {

            // [추가] 2-1. 블랙리스트 확인 (로그아웃된 토큰인지 확인)
            if (redisService.isBlacklisted(token)) {
                log.debug("JWT Token blacklisted: {}", token);
                // SecurityContext를 비우고 EntryPoint에서 401 처리되도록 ErrorCode 설정
                request.setAttribute("exception", ErrorCode.JWT_INVALID);
                filterChain.doFilter(request, response);
                return;
            }

            // 2-2. validateToken으로 토큰 유효성 검사
            TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(token);

            if (tokenResponse.errorCode() == null) {
                // 3. 토큰이 유효하면 Authentication 객체를 가지고 와서 SecurityContext에 저장
                // DB 조회를 통해 완벽한 UserDetails를 가져옴 (Dirty Checking 호환)
                UserDetails userDetails = principalDetailsService.loadUserByUsername(tokenResponse.loginId());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰 에러 발생 (만료, 서명 오류 등)
                log.debug("JWT Token invalid: {}", tokenResponse.errorCode());
                request.setAttribute("exception", tokenResponse.errorCode());
            }
        }

        filterChain.doFilter(request, response);
    }
}