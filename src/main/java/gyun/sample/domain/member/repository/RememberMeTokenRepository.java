package gyun.sample.domain.member.repository;

import gyun.sample.domain.member.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {

    Optional<RememberMeToken> findBySeries(String series);

    void deleteByUsername(String username);

}
