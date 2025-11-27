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

    @Comment("대상 회원 로그인 아이디 (탈퇴 후에도 기록 유지를 위해 문자열 저장)")
    private String loginId;

    @Comment("대상 회원 고유 ID (참조용)")
    private Long memberId;

    @Comment("작업 수행자 로그인 아이디") // 추가됨
    private String executorId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)") // Enum CHECK 제약조건 방지
    @Comment("활동 유형")
    private LogType logType;

    @Comment("상세 내용")
    private String details;

    @Comment("요청 IP")
    private String clientIp;

    // 생성자 수정
    public MemberLog(String loginId, Long memberId, String executorId, LogType logType, String details, String clientIp) {
        this.loginId = loginId;
        this.memberId = memberId;
        this.executorId = executorId; // 추가됨
        this.logType = logType;
        this.details = details;
        this.clientIp = clientIp;
    }
}