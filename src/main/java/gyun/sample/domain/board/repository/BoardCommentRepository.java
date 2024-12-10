package gyun.sample.domain.board.repository;

import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.global.enums.GlobalActiveEnums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    Page<BoardComment> getAllByMemberIdAndActive(long memberId, GlobalActiveEnums active, Pageable pageable);

    Page<BoardComment> getAllByMemberId(long memberId, Pageable pageable);
}
