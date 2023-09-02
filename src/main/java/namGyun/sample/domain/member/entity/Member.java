package namGyun.sample.domain.member.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import namGyun.sample.domain.account.entity.BaseTimeEntity;
import namGyun.sample.domain.member.payload.request.SaveMemberRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private String id;                                     //  유저 아이디
    @Column(unique = true)
    private String loginId;                                //  유저 로그인 아이디
    private String name;                                   //  유저 이름
    private String password;                               //  유저 비밀번호
    @NotNull
    private boolean active;                                 //  활성

    private LocalDateTime deletedAt;                        //  탈퇴일


    public Member(SaveMemberRequest request) {
        this.loginId = request.loginId();
        this.name = request.name();
        this.password = request.password();
        active = true;
    }
}