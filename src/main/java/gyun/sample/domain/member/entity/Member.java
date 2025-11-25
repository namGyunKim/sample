package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.global.enums.GlobalActiveEnums;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Setter
    private GlobalActiveEnums active;
    @Enumerated(EnumType.STRING)
    @Comment("유저 권한")
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Comment("유저 타입")
    private MemberType memberType;

    @Column(columnDefinition = "text")
    @Comment("소셜 토큰")
    private String socialToken; // 소셜 연동 해제 시 null 가능
    @Comment("소셜 키")
    private String socialKey;

    @Comment("JWT Refresh Token")
    @Column(columnDefinition = "text")
    private String refreshToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MemberImage> memberImages = new ArrayList<>();


    // 통합된 Request로 생성 (주로 관리자가 생성할 때 사용, role이 request에 포함됨)
    public Member(MemberCreateRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = request.password();
        // Request에 Role이 없으면 기본 USER (Validator에서 관리자는 필수 체크함)
        this.role = request.role() != null ? request.role() : AccountRole.USER;
        this.active = GlobalActiveEnums.ACTIVE;
        this.memberType = request.memberType();
    }

    // 소셜 회원가입 및 WriteUserService에서 사용
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

    // Dirty Checking을 위한 Update 메서드
    public void update(MemberUpdateRequest memberUpdateRequest) {
        this.nickName = memberUpdateRequest.nickName();
    }

    public void deActive() {
        this.active = GlobalActiveEnums.INACTIVE;
    }

    // Access Token 업데이트 (소셜 연동 해제 시 null 전달 가능)
    public void updateAccessToken(String socialToken) {
        this.socialToken = socialToken;
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
}