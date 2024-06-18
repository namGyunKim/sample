package gyun.sample.domain.member.service.read;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.BaseMemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReadAdminService extends BaseMemberService implements ReadMemberService {

    public ReadAdminService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        super(passwordEncoder, memberRepository);
    }

    @Override
    public boolean existsByRole() {
        return memberRepository.existsByRole(AccountRole.SUPER_ADMIN);
    }

    @Override
    public List<AllMemberResponse> getList() {
        List<Member> allByRole = memberRepository.findAllByRole(AccountRole.ADMIN);
        return allByRole.stream().map(AllMemberResponse::new).toList();
    }
}
