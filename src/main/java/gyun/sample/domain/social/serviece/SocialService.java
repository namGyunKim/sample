package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.enums.MemberType;
import org.springframework.stereotype.Service;

@Service
public interface SocialService<T,R,U,V> {
    T getTokenByCode(String code);
    R createOrLoginByToken(String accessToken);
    U logout(String accessToken);
    void unlink(String accessToken, MemberType memberType);
    AccountLoginResponse login(String code);
}
