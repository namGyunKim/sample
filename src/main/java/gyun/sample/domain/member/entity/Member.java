package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.CreateMemberRequest;
import gyun.sample.domain.member.payload.request.UpdateMemberRequest;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.global.enums.GlobalActiveEnums;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private GlobalActiveEnums active;                                 //  활성
    @Enumerated(EnumType.STRING)
    private AccountRole role;                               //  유저 권한

    private String profileImage;                          //  프로필 이미지

    @Enumerated(EnumType.STRING)
    private UploadDirect profileDirect;                          //  프로필 경로


    @Enumerated(EnumType.STRING)
    private MemberType memberType;                          //  유저 타입

    @Column(columnDefinition = "text")
    private String socialToken;                        //  소셜 토큰

    private String socialKey;                        //  소셜 키

    //    최고 관리자 리퀘스트로 생성
    public Member(CreateMemberRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = request.password();
        this.role = request.role();
        this.active = GlobalActiveEnums.ACTIVE;
        this.memberType = request.memberType();
    }

    //    멤버 비활성화
    public void deactivate() {
        this.active = GlobalActiveEnums.INACTIVE;
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

    public void updateProfileImage(String profileImage, UploadDirect profileDirect) {
        this.profileImage = profileImage;
        this.profileDirect = profileDirect;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void update(UpdateMemberRequest updateMemberRequest) {
        this.nickName = updateMemberRequest.nickName();
    }

    public void inactive() {
        this.active = GlobalActiveEnums.INACTIVE;
    }

    public void updateAccessToken(String accessToken) {
        this.socialToken = accessToken;
    }
}