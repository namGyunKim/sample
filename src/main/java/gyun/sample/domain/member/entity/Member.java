package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.entity.BoardComment;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.MemberAdminCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.payload.request.MemberUserCreateRequest;
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
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    @Comment("유저 아이디")
    private long id;
    @Column(unique = true, updatable = false)
    @Comment("유저 로그인 아이디")
    private String loginId;
    @Column(unique = true)
    @Comment("유저 닉네임")
    private String nickName;
    @Comment("유저 비밀번호")
    private String password;
    @Comment("유저 국가코드")
    private String countryCode;
    @Comment("유저 전화번호")
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Comment("유저 활성")
    private GlobalActiveEnums active;
    @Enumerated(EnumType.STRING)
    @Comment("유저 권한")
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Comment("유저 타입")
    private MemberType memberType;

    @Column(columnDefinition = "text")
    @Comment("소셜 토큰")
    private String socialToken;
    @Comment("소셜 키")
    private String socialKey;

    @Comment("JWT Refresh Token")
    @Column(columnDefinition = "text")
    private String refreshToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardComment> createComment = new ArrayList<>();

    @OneToMany(mappedBy = "deActiveMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardComment> deActiveComment = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MemberImage> memberImages = new ArrayList<>();


    //    최고 관리자 리퀘스트로 생성
    public Member(MemberAdminCreateRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = request.password();
        this.role = request.role();
        this.active = GlobalActiveEnums.ACTIVE;
        this.memberType = request.memberType();
    }

    //    일반 유저 리퀘스트로 생성
    public Member(MemberUserCreateRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = request.password();
        this.role = AccountRole.USER;
        this.active = GlobalActiveEnums.ACTIVE;
        this.memberType = request.memberType();
    }


    //    소셜 회원가입
    public Member(String loginId, String nickName, MemberType memberType, String socialKey) {
        this.loginId = loginId;
        this.nickName = nickName;
        this.role = AccountRole.USER;
        this.active = GlobalActiveEnums.ACTIVE;
        this.memberType = memberType;
        this.socialKey = socialKey;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void update(MemberUpdateRequest memberUpdateRequest) {
        this.nickName = memberUpdateRequest.nickName();
    }

    public void deActive() {
        this.active = GlobalActiveEnums.INACTIVE;
    }

    public void updateAccessToken(String accessToken) {
        this.socialToken = accessToken;
    }

    public void addImage(MemberImage memberImage) {
        this.memberImages.add(memberImage);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void invalidateRefreshToken() {
        this.refreshToken = null;
    }

    public void addBoard(Board savedBoard) {
        this.boards.add(savedBoard);
    }

}