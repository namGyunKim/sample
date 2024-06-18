package gyun.sample.domain.member.service.write;

import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteMemberService {

    GlobalCreateResponse createMember(CreateMemberRequest member);
}
