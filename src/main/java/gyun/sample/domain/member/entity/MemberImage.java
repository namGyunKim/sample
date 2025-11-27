package gyun.sample.domain.member.entity;


import gyun.sample.domain.account.entity.BaseTimeEntity;
import gyun.sample.domain.aws.enums.UploadDirect;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_id")
    @Comment("유저 아이디")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)") // Enum CHECK 제약조건 방지
    @Comment("이미지 경로")
    private UploadDirect uploadDirect;

    @Comment("파일이름")
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("유저 아이디")
    private Member member;


    public MemberImage(UploadDirect uploadDirect, String fileName, Member member) {
        this.uploadDirect = uploadDirect;
        this.fileName = fileName;
        this.member = member;
    }
}