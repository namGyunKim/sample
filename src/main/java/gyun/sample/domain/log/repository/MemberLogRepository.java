package gyun.sample.domain.log.repository;

import gyun.sample.domain.log.entity.MemberLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberLogRepository extends JpaRepository<MemberLog, Long> {

    /**
     * 로그인 ID로 로그 검색 (부분 일치)
     * 대소문자 구분 없이 검색 (IgnoreCase)
     */
    Page<MemberLog> findByLoginIdContainingIgnoreCase(String loginId, Pageable pageable);
}