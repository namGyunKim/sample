package gyun.sample.global.config.security;

import gyun.sample.domain.member.entity.RememberMeToken;
import gyun.sample.domain.member.repository.RememberMeTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class CustomPersistentTokenRepository implements PersistentTokenRepository {

    private final RememberMeTokenRepository tokenRepository;

    @Override
    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeToken rememberMeToken = new RememberMeToken(token);
        tokenRepository.save(rememberMeToken);
    }

    @Override
    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        Optional<RememberMeToken> optionalToken = tokenRepository.findBySeries(series);
        if (optionalToken.isPresent()) {
            RememberMeToken rememberMeToken = optionalToken.get();
            rememberMeToken.updateToken(tokenValue, lastUsed.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
            tokenRepository.save(rememberMeToken);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        Optional<RememberMeToken> optionalToken = tokenRepository.findBySeries(seriesId);
        if (optionalToken.isPresent()) {
            RememberMeToken token = optionalToken.get();
            return new PersistentRememberMeToken(
                    token.getUsername(),
                    token.getSeries(),
                    token.getToken(),
                    java.util.Date.from(token.getLastUsed()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant())
            );
        }
        return null;
    }

    @Override
    @Transactional
    public void removeUserTokens(String username) {
        tokenRepository.deleteByUsername(username);
    }
}
