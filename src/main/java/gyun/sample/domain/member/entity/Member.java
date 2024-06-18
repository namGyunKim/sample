package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;                                     //  유저 아이디 및 소셜키
    @Column(unique = true, updatable = false)
    private String loginId;                                //  유저 로그인 아이디
    @Column(unique = true)
    private String nickName;                                   //  닉네임
    private String password;                               //  유저 비밀번호
    @NotNull
    private boolean active;                                 //  활성
    @Enumerated(EnumType.STRING)
    private AccountRole role;                               //  유저 권한


    @Enumerated(EnumType.STRING)
    private MemberType memberType;                          //  유저 타입

    //    최고 관리자 리퀘스트로 생성
    public Member(CreateMemberRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = request.password();
        this.role = request.role();
        this.active = true;
        this.memberType = request.memberType();
    }

    //    멤버 비활성화
    public void deactivate() {
        this.active = false;
    }

    //    소셜 회원가입
    public Member(String socialKey, String nickName, MemberType memberType) {
        this.loginId = socialKey;
        this.nickName = nickName;
        this.role = AccountRole.CUSTOMER;
        this.active = true;
        this.memberType = memberType;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}