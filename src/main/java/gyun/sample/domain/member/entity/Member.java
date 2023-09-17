package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.admin.SaveMemberForSuperAdminRequest;
import gyun.sample.domain.member.payload.request.customer.SaveCustomerForSelfRequest;
import gyun.sample.domain.member.payload.request.customer.UpdateCustomerForSelfRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: 2023/09/11 Member Entity filed 논의가 필요 
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private String id;                                     //  유저 아이디 및 소셜키
    @Column(unique = true,updatable = false)
    private String loginId;                                //  유저 로그인 아이디
    private String nickName;                                   //  닉네임
    private String password;                               //  유저 비밀번호
    @NotNull
    private boolean active;                                 //  활성
    @Enumerated(EnumType.STRING)
    private AccountRole role;                               //  유저 권한

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>(); //  유저 주소


    @Enumerated(EnumType.STRING)
    private MemberType memberType;                          //  유저 타입

    //    고객 리퀘스트로 생성
    public Member(SaveCustomerForSelfRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = passwordEncoding(request.password());
        this.role = AccountRole.CUSTOMER;
        this.active = true;
        this.memberType = MemberType.GENERAL;
    }

    //    비밀번호 암호화
    private String passwordEncoding(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //    최고 관리자 리퀘스트로 생성
    public Member (SaveMemberForSuperAdminRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = passwordEncoding(request.password());
        this.role = AccountRole.SUPER_ADMIN;
        this.active = true;
        this.memberType = MemberType.GENERAL;
    }

//    고객이 자신의 정보 수정
    public void update(UpdateCustomerForSelfRequest request) {
        this.nickName = request.nickName();
        if(!StringUtils.isBlank(request.password())){
            this.password = passwordEncoding(request.password());
        }
    }

//    멤버 비활성화
    public void deactivate() {
        this.active = false;
    }

//    소셜 회원가입
    public Member(String socialKey, String nickName,MemberType memberType) {
        this.loginId = socialKey;
        this.nickName = nickName;
        this.role = AccountRole.CUSTOMER;
        this.active = true;
        this.memberType = memberType;
    }
}