package gyun.sample.domain.member.repository.custom;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<Member> getMemberList(GetMemberListRequest getMemberListRequest, AccountRole accountRole, Pageable pageable);
}
