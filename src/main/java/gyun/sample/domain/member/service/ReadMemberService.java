package gyun.sample.domain.member.service;

import org.springframework.stereotype.Service;

@Service
public interface ReadMemberService {

    boolean existsByRole();
}
