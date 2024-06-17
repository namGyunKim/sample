package gyun.sample.domain.member.service;

import gyun.sample.domain.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface WriteMemberService {

    Member saveMember(Member member);
}
