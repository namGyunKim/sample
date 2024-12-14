package gyun.sample.domain.member.repository.custom;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.MemberAdminListRequest;
import gyun.sample.domain.member.payload.request.MemberUserListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    Page<Member> getMemberAdminList(MemberAdminListRequest memberAdminListRequest, List<AccountRole> accountRoles, Pageable pageable);

    Page<Member> getMemberUserList(MemberUserListRequest memberUserListRequest, List<AccountRole> accountRoles, Pageable pageable);
}
