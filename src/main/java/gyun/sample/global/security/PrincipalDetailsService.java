package gyun.sample.global.security;

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

    // 로그인 아이디로 유저 정보 로드 (Spring Security Form Login의 핵심)
    // Spring Security의 기본 Form Login은 'username' 파라미터만 사용하여 이 메서드를 호출합니다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // [수정] Form Login 시 `role` 파라미터를 함께 전달받지 못하므로,
            // ReadAccountService는 `loginId`만으로 찾도록 합니다.
            // *단, ReadAccountService::findByLoginId는 Active 체크를 수행합니다.*
            Member member = readAccountService.findByLoginId(username);
            return new PrincipalDetails(member);
        } catch (GlobalException e) {
            // GlobalException을 Spring Security가 처리할 수 있는 UsernameNotFoundException으로 변환
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username, e);
        }
    }
}