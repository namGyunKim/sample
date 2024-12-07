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
import org.hibernate.annotations.Comment;

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
    @Comment("이미지 경로")
    final private UploadDirect imageDirect = UploadDirect.MEMBER_PROFILE;

    @Comment("이미지 확장자")
    private String imageExtension;


    @Enumerated(EnumType.STRING)
    @Comment("유저 타입")
    private MemberType memberType;

    @Column(columnDefinition = "text")
    @Comment("소셜 토큰")
    private String socialToken;
    @Comment("소셜 키")
    private String socialKey;

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

    public void updateProfileExtension(String extension) {
        this.imageExtension = extension;
    }
}