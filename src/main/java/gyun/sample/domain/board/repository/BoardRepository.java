package gyun.sample.domain.board.repository;

import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.repository.custom.BoardRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

}
