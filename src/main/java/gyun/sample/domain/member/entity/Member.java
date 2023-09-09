package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.request.SaveMemberForSuperAdminRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private String id;                                     //  유저 아이디
    @Column(unique = true)
    private String loginId;                                //  유저 로그인 아이디
    private String name;                                   //  유저 이름
    private String password;                               //  유저 비밀번호
    @NotNull
    private boolean active;                                 //  활성
    @Enumerated(EnumType.STRING)
    private AccountRole role;                               //  유저 권한

    //    고객 리퀘스트로 생성
    public Member(SaveMemberForCustomerRequest request) {
        this.loginId = request.loginId();
        this.name = request.name();
        this.password = passwordEncoding(request.password());
        this.role = AccountRole.CUSTOMER;
        active = true;
    }

    //    비밀번호 암호화
    private String passwordEncoding(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //    최고 관리자 리퀘스트로 생성
    public Member (SaveMemberForSuperAdminRequest request) {
        this.loginId = request.loginId();
        this.name = request.name();
        this.password = passwordEncoding(request.password());
        this.role = AccountRole.SUPER_ADMIN;
        active = true;
    }

}