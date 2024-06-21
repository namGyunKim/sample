package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class BaseSocialService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Member getWithSocial(String loginId, AccountRole accountRole, GlobalActiveEnums active, MemberType memberType, String nickName, String accessToken, String socialKey) {
        // 가입 여부 확인
        Member member = memberRepository.findBySocialKeyAndRoleAndActiveAndMemberType(
                socialKey, accountRole, active, memberType).orElseGet(() -> {
            // 회원이 존재하지 않을 경우 회원가입 처리
            Member newMember = new Member(loginId, nickName, memberType, socialKey);
            newMember.updateAccessToken(accessToken);
            return memberRepository.save(newMember);
        });
        member.updateAccessToken(accessToken);
        return member;
    }

    // 소셜 계정 로그인
    public AccountLoginResponse login(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);
        return new AccountLoginResponse(accessToken, refreshToken);
    }
}
