package gyun.sample.domain.member.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    protected final MemberRepository memberRepository;



    @Transactional
    public void saveMemberByRole(Member member){
        memberRepository.save(member);
    }

    public boolean existByRoleSuperAdmin(){
        return memberRepository.existByRoleSuperAdmin();
    }
}
