package gyun.sample.domain.member.service.write;

import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.payload.request.admin.UpdateMemberRequest;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteMemberService {

    GlobalCreateResponse createMember(CreateMemberRequest member);

    GlobalUpdateResponse updateMember(UpdateMemberRequest updateMemberRequest,String loginId);
}
