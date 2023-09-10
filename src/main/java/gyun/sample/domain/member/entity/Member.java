package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private String id;                                     //  유저 아이디
    @Column(unique = true,updatable = false)
    private String loginId;                                //  유저 로그인 아이디
    private String name;                                   //  유저 이름
    private String password;                               //  유저 비밀번호
    @NotNull
    private boolean active;                                 //  활성
    @Enumerated(EnumType.STRING)
    private AccountRole role;                               //  유저 권한

    //    고객 리퀘스트로 생성
    public Member(SaveCustomerForSelfRequest request) {
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

    public void update(UpdateCustomerForSelfRequest request) {
        this.name = request.name();
        if(!StringUtils.isBlank(request.password())){
            this.password = passwordEncoding(request.password());
        }
    }

//    멤버 비활성화
    public void deactivate() {
        this.active = false;
    }
}