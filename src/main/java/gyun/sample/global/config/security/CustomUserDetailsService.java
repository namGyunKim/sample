package gyun.sample.global.config.security;

import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * "DB에서 Member 엔티티를 조회해,
 * UserDetails(=CustomUserDetails)로 감싸 반환"하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository; // 실제 DB 접근

    /**
     * 로그인 시도 시, DB에서 회원 조회 → UserDetails 리턴
     *
     * @param username 로그인 시 입력된 사용자 ID (loginId)
     * @return UserDetails
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByLoginId(username)
                .filter(member -> GlobalActiveEnums.ACTIVE == member.getActive())
                .map(CustomUserDetails::new)
                .orElseThrow(() -> {
                    log.info("로그인 시도: {} - 실패 (사용자를 찾을 수 없거나 비활성화된 계정)", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없거나 비활성화된 계정입니다.");
                });
    }
}
