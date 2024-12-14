package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import gyun.sample.domain.member.payload.response.MemberListResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReadMemberService<T> {

    boolean existsByRole(AccountRole accountRole);

    Page<MemberListResponse> getList(T memberUserListRequest);

    DetailMemberResponse getDetail(long id);


    Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles);

    Member getByLoginIdAndRole(String loginId, AccountRole role);
}
