package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberProfileListDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberListResponse {

    private MemberProfileListDTO profile;


    public MemberListResponse(Member member) {
        this.profile = new MemberProfileListDTO(member);
    }
}