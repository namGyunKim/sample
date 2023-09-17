package gyun.sample.domain.board.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.SaveBoardRequest;
import gyun.sample.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "free_board_id")
    private String id;                                     //  유저 아이디 및 소셜키
    @NotNull
    private boolean active;                                 //  활성

    private String title;                                   //  제목
    @Column(columnDefinition = "TEXT")
    private String content;                                 //  내용

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;                                  //  작성자

    @Enumerated(EnumType.STRING)
    private BoardType boardType;                            //  게시판 타입


    //    익명 게시판 생성
    public Board(SaveBoardRequest request) {
        this.title = request.title();
        this.content = request.content();
        this.boardType = request.boardType();
        this.active = true;
    }

    //    익명이 아닌 게시판
    public Board(SaveBoardRequest request, Member member) {
        this.title = request.title();
        this.content = request.content();
        this.boardType = request.boardType();
        this.active = true;
        this.member = member;
        this.member.getBoardList().add(this);
    }
}