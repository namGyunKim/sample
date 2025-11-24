package gyun.sample.global.security;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.service.ReadAccountService;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final ReadAccountService readAccountService;

    // 로그인 아이디로 유저 정보 로드
    // 여기서는 실제 DB 조회를 수행
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // ReadAccountService의 검증 로직(Active 체크 등)을 그대로 활용
            Member member = readAccountService.findByLoginId(username);
            return new PrincipalDetails(member);
        } catch (GlobalException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    // JWT 토큰 파싱 후 ID/Role 만으로 객체를 만들 때 사용 (DB 조회 없이 SecurityContext 구성용)
    public UserDetails loadUserByClaims(Long id, String loginId, AccountRole role) {
        // DB 조회를 안하고 토큰 정보만으로 인증 객체를 만들고 싶을 때 사용
        // 단, 이 경우 Member 엔티티가 완전하지 않으므로 주의 필요.
        // Dirty Checking 등을 위해 영속성 컨텍스트가 필요하다면 loadUserByUsername을 타는게 맞음.
        // 성능을 위해 DB 조회를 줄이려면 캐싱을 고려.
        // 현재 구조상 안전하게 DB 조회를 태움.
        return loadUserByUsername(loginId);
    }
}