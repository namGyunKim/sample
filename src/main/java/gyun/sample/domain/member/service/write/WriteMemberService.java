package gyun.sample.domain.member.service.write;

import gyun.sample.domain.member.payload.request.UpdateMemberRequest;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteMemberService<T> {

    GlobalCreateResponse createMember(T member);

    GlobalUpdateResponse updateMember(UpdateMemberRequest updateMemberRequest, String loginId);

    GlobalInactiveResponse inactiveMember(String loginId);

}