package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.admin.AllMemberRequest;
import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import gyun.sample.domain.member.payload.response.admin.DetailMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ReadMemberService {

    boolean existsByRole(AccountRole accountRole);

    Page<AllMemberResponse> getList(AllMemberRequest allMemberRequest);

    DetailMemberResponse getDetail(long id);
}
