package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ReadMemberService {

    boolean existsByRole(AccountRole accountRole);

    Page<AllMemberResponse> getList(GetMemberListRequest getMemberListRequest);

}
