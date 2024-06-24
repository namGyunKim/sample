package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.global.enums.GlobalActiveEnums;
import org.springframework.stereotype.Service;

@Service
public interface SocialService<T,R,U,V> {
    T getTokenByCode(String code);
    R createOrLoginByToken(String accessToken);
    U logout(String accessToken);
    U unlink(String accessToken);
    AccountLoginResponse login(String code);

    Member getWithSocial(String loginId, AccountRole accountRole, GlobalActiveEnums active, MemberType memberType, String nickName, String accessToken, String socialKey);

    // 소셜 계정 로그인
    AccountLoginResponse login(Member member);
}
