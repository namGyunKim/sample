package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.AllMemberProfileDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AllMemberResponse {

    private AllMemberProfileDTO profile;
    private AllMemberProfileDTO profile2;


    public AllMemberResponse(Member member) {
        this.profile = new AllMemberProfileDTO(member);
        this.profile2 = new AllMemberProfileDTO(member);
    }
}