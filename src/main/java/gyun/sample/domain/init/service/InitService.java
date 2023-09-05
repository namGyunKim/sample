package gyun.sample.domain.init.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForSuperAdminRequest;
import gyun.sample.domain.member.service.AdminService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitService {

    private final AdminService adminService;

    @PostConstruct
    public void init() {
        saveMemberByRoleSuperAdmin();
    }


    //    최고관리자가 없을경우 생성
    @Transactional
    public void saveMemberByRoleSuperAdmin() {
        if (!adminService.existByRole(AccountRole.SUPER_ADMIN)) {
            SaveMemberForSuperAdminRequest request = new SaveMemberForSuperAdminRequest("superAdmin", "최고관리자", "1234");
            Member member = new Member(request);
            adminService.saveMember(member);
        }
    }

}
