package gyun.sample.domain.init.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.CreateMemberAdminRequest;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitService {

    //    service
    private final ReadMemberService readAdminService;
    private final WriteMemberService writeAdminService;

    //    서버 시작시 실행
    @PostConstruct
    public void init() {
        createMemberByRoleSuperAdmin();
        createMemberByRoleAdmin();
        createMemberByRoleUser();
    }


    //    최고 관리자가 없을경우 생성
    @Transactional
    public void createMemberByRoleSuperAdmin() {
        if (!readAdminService.existsByRole(AccountRole.SUPER_ADMIN)) {
            CreateMemberAdminRequest request = new CreateMemberAdminRequest("superAdmin", "최고관리자", "1234", AccountRole.SUPER_ADMIN, MemberType.GENERAL);
            writeAdminService.createMember(request);
        }
    }

    //    관리자가 없을경우 관리자 10개 생성
    @Transactional
    public void createMemberByRoleAdmin() {
        if (!readAdminService.existsByRole(AccountRole.ADMIN)) {
            for (int i = 1; i <= 10; i++) {
                CreateMemberAdminRequest request = new CreateMemberAdminRequest("admin" + i, "관리자" + i, "1234", AccountRole.ADMIN, MemberType.GENERAL);
                writeAdminService.createMember(request);
            }
        }
    }

    //    유저가 없을경우 관리자 10개 생성
    @Transactional
    public void createMemberByRoleUser() {
        if (!readAdminService.existsByRole(AccountRole.USER)) {
            for (int i = 1; i <= 10; i++) {
                CreateMemberAdminRequest request = new CreateMemberAdminRequest("user" + i, "유저이름" + i, "1234", AccountRole.USER, MemberType.GENERAL);
                writeAdminService.createMember(request);
            }
        }
    }

}
