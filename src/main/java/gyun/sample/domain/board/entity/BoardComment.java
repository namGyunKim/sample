package gyun.sample.domain.board.entity;

import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.board.payload.request.CreateCommentRequest;
import gyun.sample.domain.board.payload.request.InactiveCommentRequest;
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
public class BoardComment extends BaseTimeEntity {
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardComment> replies = new ArrayList<>(); // 자식 댓글
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_comment_id")
    @Comment("댓글 아이디")
    private long id;
    @Column(columnDefinition = "text")
    @Comment("댓글 내용")
    private String content;
    @Enumerated(EnumType.STRING)
    @Comment("활성화 여부")
    private GlobalActiveEnums active; // 활성화 여부
    @Comment("비활성화 사유")
    private String inactiveReason; // 비활성화 사유
    @Comment("작성자 IP")
    private String createIp; // 작성자 IP
    @Comment("비활성화 IP")
    private String inactiveIp; // 비활성화 IP
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @Comment("게시판")
    private Board board; // 게시판
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("작성자")
    private Member member; // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inactive_by_member_id")
    @Comment("비활성화 한 사람")
    private Member deActiveMember; // 비활성화 한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment("부모 댓글")
    private BoardComment parent;


    public BoardComment(CreateCommentRequest request, Board board, Member member, BoardComment parent, String createIp) {
        this.content = request.content();
        this.board = board;
        this.member = member;
        this.parent = parent;
        this.active = GlobalActiveEnums.ACTIVE;
        this.createIp = createIp;
    }

    public void addReply(BoardComment reply) {
        this.replies.add(reply);
        reply.parent = this;
    }

    public void deActive(InactiveCommentRequest request, Member member, String inactiveIp) {
        this.active = GlobalActiveEnums.INACTIVE;
        this.inactiveReason = request.inactiveReason();
        this.deActiveMember = member;
        this.inactiveIp = inactiveIp;
    }
}