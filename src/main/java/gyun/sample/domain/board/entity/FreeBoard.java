package gyun.sample.domain.board.entity;

import gyun.sample.domain.account.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeBoard extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "free_board_id")
    @Comment("게시판 아이디")
    private long id; // 게시판 아이디


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "freeBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("게시글")
    private Board board; // 게시글

    public FreeBoard(Board board) {
        this.board = board;
    }

}