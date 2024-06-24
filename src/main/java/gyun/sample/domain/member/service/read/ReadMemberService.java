package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.AllMemberRequest;
import gyun.sample.domain.member.payload.response.AllMemberResponse;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReadMemberService {

    boolean existsByRole(AccountRole accountRole);

    Page<AllMemberResponse> getList(AllMemberRequest allMemberRequest);

    DetailMemberResponse getDetail(long id);


    Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles);

    Member getByLoginIdAndRole(String loginId, AccountRole role);
}
