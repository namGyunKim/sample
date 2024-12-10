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
public class QuestionBoard extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_board_id")
    @Comment("게시판 아이디")
    private long id; // 게시판 아이디


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "questionBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Board board; // 게시글


    public QuestionBoard(Board board) {
        this.board = board;
    }

}