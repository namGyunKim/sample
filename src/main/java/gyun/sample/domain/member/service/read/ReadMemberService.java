package gyun.sample.domain.member.service.read;

import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReadMemberService {

    boolean existsByRole();
    List<AllMemberResponse> getList();

}
