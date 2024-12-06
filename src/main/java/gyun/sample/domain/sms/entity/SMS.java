package gyun.sample.domain.sms.entity;

import gyun.sample.domain.account.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SMS extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_id")
    @Comment("SMS 아이디")
    private long id;

    @Comment("전화번호")
    private String phoneNumber;

    @Comment("인증코드")
    private String verificationCode;

    @Comment("인증여부")
    private boolean verified;

    public SMS(String phoneNumber, String verificationCode) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
    }

    public void verify() {
        this.verified = true;
    }

}
