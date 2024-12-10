package gyun.sample.domain.board.entity;

import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.CreateBoardRequest;
import gyun.sample.domain.board.payload.request.UpdateBoardRequest;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.global.enums.GlobalActiveEnums;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    @Comment("게시판 아이디")
    private long id;
    @Comment("게시글 제목")
    private String title;
    @Column(columnDefinition = "text")
    @Comment("게시글 내용")
    private String content;
    @Comment("조회수")
    private long viewCount;
    @Comment("추천수")
    private long likeCount;
    @Comment("공지 여부")
    private boolean notice;
    @Column(columnDefinition = "text")
    @Comment("비활성화 사유")
    private String inactiveReason;
    @Enumerated(EnumType.STRING)
    @Comment("게시판 타입")
    private BoardType boardType;
    @Enumerated(EnumType.STRING)
    @Comment("활성화 여부")
    private GlobalActiveEnums active;
    @Comment("작성자 IP")
    private String createIp;
    @Comment("비활성화 IP")
    private String inactiveIp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("작성자")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inactive_by_member_id")
    @Comment("비활성화 한 사람")
    private Member inactiveBy;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_board_id")
    @Comment("자유 게시판")
    private FreeBoard freeBoard; // 자유 게시판
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_board_id")
    @Comment("질문 게시판")
    private QuestionBoard questionBoard; // 질문 게시판

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardComment> comments = new ArrayList<>(); // 댓글

    public Board(CreateBoardRequest request, Member member, String createIp) {
        this.title = request.title();
        this.content = request.content();
        this.notice = request.notice();
        this.boardType = request.boardType();
        this.active = GlobalActiveEnums.ACTIVE;
        this.member = member;
        this.createIp = createIp;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void addQuestion(QuestionBoard questionBoard) {
        this.questionBoard = questionBoard;
    }

    public void addFree(FreeBoard freeBoard) {
        this.freeBoard = freeBoard;
    }

    public void update(UpdateBoardRequest request) {
        this.title = request.title();
        this.content = request.content();
        this.notice = request.notice();
        this.boardType = request.boardType();
    }

    public void inactive(String inactiveReason, Member member, String inactiveIp) {
        this.active = GlobalActiveEnums.INACTIVE;
        this.inactiveReason = inactiveReason;
        this.inactiveBy = member;
        this.inactiveIp = inactiveIp;
    }

    public void addComment(BoardComment comment) {
        this.comments.add(comment);
    }
}