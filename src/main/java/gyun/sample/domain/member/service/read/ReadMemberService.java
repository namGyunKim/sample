package gyun.sample.domain.member.service.read;

import org.springframework.stereotype.Service;

@Service
public interface ReadMemberService {

    boolean existsByRole();
}
