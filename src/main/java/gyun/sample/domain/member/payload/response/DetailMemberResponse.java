package gyun.sample.domain.member.payload.response;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberProfileDetailDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetailMemberResponse {

    private MemberProfileDetailDTO profile;

    public DetailMemberResponse(Member member) {
        this.profile = new MemberProfileDetailDTO(member);
    }
}