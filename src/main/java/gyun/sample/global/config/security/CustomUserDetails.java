package gyun.sample.global.config.security;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.enums.GlobalActiveEnums;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * "한 명의 사용자 정보(아이디, 비밀번호, 권한 등)"를 보관하는 클래스
 * Spring Security의 UserDetails 인터페이스를 구현
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member; // 실제 엔티티를 보관

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    /**
     * 권한(Authorities)을 반환
     * 'ROLE_' 접두어를 붙여 스프링 시큐리티 표준에 맞춤
     */
    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        // 예: member.getRole() == AccountRole.USER → "ROLE_USER"
        String roleName = "ROLE_" + member.getRole().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    /**
     * 스프링 시큐리티가 내부적으로 비밀번호 검증 시 사용하는 값
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 스프링 시큐리티에서 사용하는 '사용자 이름'
     * 여기서는 회원의 loginId가 곧 username
     */
    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    /**
     * 계정 만료 여부 (true 면 만료 아님)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 (true 면 잠금 아님)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부 (true 면 만료 아님)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부 (true 면 활성)
     */
    @Override
    public boolean isEnabled() {
        return member.getActive() != null && member.getActive() == GlobalActiveEnums.ACTIVE;
    }
}
