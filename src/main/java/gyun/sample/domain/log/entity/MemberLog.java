package gyun.sample.domain.log.entity;

import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.log.enums.LogType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_log")
public class MemberLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Comment("로그인 아이디 (탈퇴 후에도 기록 유지를 위해 문자열 저장)")
    private String loginId;

    @Comment("회원 고유 ID (참조용)")
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Comment("활동 유형")
    private LogType logType;

    @Comment("상세 내용")
    private String details;

    @Comment("요청 IP")
    private String clientIp;

    // 생성자
    public MemberLog(String loginId, Long memberId, LogType logType, String details, String clientIp) {
        this.loginId = loginId;
        this.memberId = memberId;
        this.logType = logType;
        this.details = details;
        this.clientIp = clientIp;
    }
}