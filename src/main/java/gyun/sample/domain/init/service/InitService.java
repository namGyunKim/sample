package gyun.sample.domain.init.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.SaveMemberForSuperAdminRequest;
import gyun.sample.domain.member.payload.request.customer.SaveCustomerForSelfRequest;
import gyun.sample.domain.member.service.AdminService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitService {

    //    service
    private final AdminService adminService;

    //    서버 시작시 실행
    @PostConstruct
    public void init() {
        saveMemberByRoleSuperAdmin();
        saveMemberByRoleCustomer();
    }


    //    최고 관리자가 없을경우 생성
    @Transactional
    public void saveMemberByRoleSuperAdmin() {
        if (!adminService.existsByRole(AccountRole.SUPER_ADMIN)) {
            SaveMemberForSuperAdminRequest request = new SaveMemberForSuperAdminRequest("superAdmin", "최고관리자", "1234");
            Member member = new Member(request);
            adminService.saveMember(member);
        }
    }

    //    고객이 없을경우 생성
    @Transactional
    public void saveMemberByRoleCustomer() {
        if (!adminService.existsByRole(AccountRole.CUSTOMER)) {
            SaveCustomerForSelfRequest request = new SaveCustomerForSelfRequest("skarbs01", "최초의 고객", "1234");
            Member member = new Member(request);
            adminService.saveMember(member);
        }
    }
}
