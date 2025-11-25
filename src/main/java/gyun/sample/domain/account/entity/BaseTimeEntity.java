package gyun.sample.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 상위 클래스로, 생성일/수정일/생성자/수정자를 자동으로 관리합니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    @Comment("생성일")
    private LocalDateTime createdAt;            // 생성일

    @CreatedBy
    @Column(updatable = false)
    @Comment("생성자")
    private String createdBy;                   // 생성자 (로그인 ID)

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime modifiedAt;           // 수정일

    @LastModifiedBy
    @Comment("수정자")
    private String lastModifiedBy;              // 수정자 (로그인 ID)
}