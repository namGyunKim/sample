package gyun.sample.domain.log.repository;

import gyun.sample.domain.log.entity.MemberLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberLogRepository extends JpaRepository<MemberLog, Long> {
}