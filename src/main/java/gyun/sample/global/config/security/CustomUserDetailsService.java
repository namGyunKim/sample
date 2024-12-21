package gyun.sample.global.config.security;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
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
     * 로그인 시도 시, 여기서 DB에서 회원 조회 → UserDetails 리턴
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username = loginId 라고 가정
        log.info("로그인 시도: {}", username);
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST, username));

        // 찾은 Member를 CustomUserDetails로 감싸서 반환
        return new CustomUserDetails(member);
    }
}
