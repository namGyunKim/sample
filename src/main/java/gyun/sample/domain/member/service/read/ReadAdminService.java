package gyun.sample.domain.member.service.read;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.BaseMemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReadAdminService extends BaseMemberService implements ReadMemberService {

    public ReadAdminService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        super(passwordEncoder, memberRepository);
    }

    @Override
    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    @Override
    public Page<AllMemberResponse> getList(GetMemberListRequest request) {
        Pageable pageable = PageRequest.of(request.page() - 1, request.size());
        Page<Member> memberList = memberRepository.getMemberList(request, AccountRole.ADMIN, pageable);
        return memberList.map(AllMemberResponse::new);
    }
}
