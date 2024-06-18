package gyun.sample.domain.init.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
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
            CreateMemberRequest request = new CreateMemberRequest("superAdmin", "최고관리자", "1234", AccountRole.SUPER_ADMIN, MemberType.GENERAL);
            writeMemberService.createMember(request);
        }
    }

}
