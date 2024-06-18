package gyun.sample.domain.member.service.read;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.BaseMemberService;
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
    public boolean existsByRole() {
        return memberRepository.existsByRole(AccountRole.SUPER_ADMIN);
    }


}
