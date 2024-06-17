package gyun.sample.domain.member.service;


import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class WriteAdminService extends BaseMemberService implements WriteMemberService {


    public WriteAdminService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        super(passwordEncoder, memberRepository);
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }
}
