package gyun.sample.domain.board.repository;

import gyun.sample.domain.board.entity.QuestionBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, Long> {

}
