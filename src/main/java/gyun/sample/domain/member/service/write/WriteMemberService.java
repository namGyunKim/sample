package gyun.sample.domain.member.service.write;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteMemberService {

    GlobalCreateResponse createMember(MemberCreateRequest request);

    GlobalUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, String loginId);

    GlobalInactiveResponse deActiveMember(String loginId);

    void updateMemberRole(String loginId, AccountRole newRole);

}