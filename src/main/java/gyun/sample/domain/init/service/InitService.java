package gyun.sample.domain.init.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.SaveMemberWithSuperAdminRequest;
import gyun.sample.domain.member.service.ReadMemberService;
import gyun.sample.domain.member.service.WriteMemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitService {

    //    service
    private final ReadMemberService readAdminService;
    private final PasswordEncoder passwordEncoder;
    private final WriteMemberService writeMemberService;
    //    서버 시작시 실행
    @PostConstruct
    public void init() {
        saveMemberByRoleSuperAdmin();
    }


    //    최고 관리자가 없을경우 생성
    @Transactional
    public void saveMemberByRoleSuperAdmin() {
        if (!readAdminService.existsByRole()) {
            final String password = passwordEncoder.encode("1234");
            SaveMemberWithSuperAdminRequest request = new SaveMemberWithSuperAdminRequest("superAdmin", "최고관리자", password);
            Member member = new Member(request);
            writeMemberService.saveMember(member);
        }
    }

}
