package gyun.sample.domain.board.repository;


import gyun.sample.domain.board.entity.Board;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Board> findByIdAndActiveTrue(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Board> findById(String id);

}
