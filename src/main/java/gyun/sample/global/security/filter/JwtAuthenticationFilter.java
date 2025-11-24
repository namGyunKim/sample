package gyun.sample.global.security.filter;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.global.security.PrincipalDetailsService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);

        // 2. validateToken으로 토큰 유효성 검사
        if (token != null) {
            // TokenResponse 안에 에러코드가 없으면 유효한 토큰
            TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(token);

            if (tokenResponse.errorCode() == null) {
                // 3. 토큰이 유효하면 Authentication 객체를 가지고 와서 SecurityContext에 저장
                // DB 조회를 통해 완벽한 UserDetails를 가져옴 (데이터 무결성 및 Dirty Checking 호환)
                UserDetails userDetails = principalDetailsService.loadUserByUsername(tokenResponse.loginId());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰 에러가 있지만, SecurityContext를 비워두면
                // 뒤따르는 EntryPoint에서 401 처리를 하거나, 권한이 필요 없는 요청은 통과됨.
                log.debug("JWT Token invalid: {}", tokenResponse.errorCode());
                request.setAttribute("exception", tokenResponse.errorCode());
            }
        }

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}