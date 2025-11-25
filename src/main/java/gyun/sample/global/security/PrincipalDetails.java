package gyun.sample.global.security;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Spring Security에서 사용하는 인증 객체 구현체입니다.
 * 타임리프에서 sec:authentication="principal.nickName" 처럼
 * 이 클래스의 필드나 Getter 메서드에 직접 접근할 수 있습니다.
 */
@Getter
public class PrincipalDetails implements UserDetails {

    private final Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    // 타임리프에서 principal.nickName으로 호출될 때 사용됩니다.
    public String getNickName() {
        return member.getNickName();
    }

    public Long getId() {
        return member.getId();
    }

    public AccountRole getRole() {
        return member.getRole();
    }

    public MemberType getMemberType() {
        return member.getMemberType();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 필요하다면 member.getActive() == GlobalActiveEnums.ACTIVE 체크를 추가할 수 있습니다.
        return true;
    }
}