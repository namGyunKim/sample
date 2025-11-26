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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    @Comment("유저 아이디")
    private long id;

    @Column(unique = true) // 변경 가능하도록 updatable = false 제거 (탈퇴 시 변경 위해)
    @Comment("유저 로그인 아이디")
    private String loginId;

    @Column(unique = true)
    @Comment("유저 닉네임")
    private String nickName;

    @Comment("유저 비밀번호")
    private String password;

    @Enumerated(EnumType.STRING)
    @Comment("유저 활성 상태")
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
    private final transient List<MemberImage> memberImages = new ArrayList<>();

    // 생성자: 관리자 생성용
    public Member(MemberCreateRequest request) {
        this.loginId = request.loginId();
        this.nickName = request.nickName();
        this.password = request.password();
        this.role = request.role() != null ? request.role() : AccountRole.USER;
        this.active = GlobalActiveEnums.ACTIVE;
        this.memberType = request.memberType();
    }

    // 생성자: 소셜 로그인용
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

    // 더티 체킹을 위한 회원 정보 수정
    public void update(MemberUpdateRequest request) {
        this.nickName = request.nickName();
    }

    // 회원 탈퇴 처리 (Soft Delete + Unique Key 회피)
    public void withdraw() {
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.active = GlobalActiveEnums.INACTIVE;
        this.loginId = this.loginId + "_LEAVE_" + nowStr;
        this.nickName = this.nickName + "_LEAVE_" + nowStr;
        this.socialToken = null;
        this.refreshToken = null;
        // socialKey는 유지하되, 재가입 시 중복 체크 로직에서 Active 상태인 것만 조회하도록 쿼리 조정 필요
    }

    public void updateAccessToken(String socialToken) {
        this.socialToken = socialToken;
    }

    public void invalidateRefreshToken() {
        this.refreshToken = null;
    }

    // Active 상태 변경 Setter (필요 시 사용)
    public void setActive(GlobalActiveEnums active) {
        this.active = active;
    }

    public void changeRole(AccountRole newRole) {
        this.role = newRole;
    }
}