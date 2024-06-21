package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface SocialService<T,R,U,V> {
    T getTokenByCode(String code);
    R createOrLoginByToken(String accessToken);
    U logout(String accessToken);
    U unlink(String accessToken);
    AccountLoginResponse login(String code);
}
