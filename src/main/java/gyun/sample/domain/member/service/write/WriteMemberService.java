package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.payload.request.admin.UpdateMemberRequest;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteMemberService {

    GlobalCreateResponse createMember(CreateMemberRequest member);

    GlobalUpdateResponse updateMember(UpdateMemberRequest updateMemberRequest, String loginId);

    GlobalInactiveResponse inactiveMember(String loginId);

    Member getWithSocial(String loginId, AccountRole accountRole, GlobalActiveEnums active, MemberType memberType, String nickName,String accessToken);
}
