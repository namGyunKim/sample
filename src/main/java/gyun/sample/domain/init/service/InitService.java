package gyun.sample.domain.init.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.enums.MemberType;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.service.MemberStrategyFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitService {

    // MemberStrategyFactory를 주입받아 역할별 서비스를 동적으로 가져옵니다.
    private final MemberStrategyFactory memberStrategyFactory;

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
        if (!memberStrategyFactory.getReadService(AccountRole.SUPER_ADMIN).existsByRole(AccountRole.SUPER_ADMIN)) {
            // 통합된 MemberCreateRequest 사용
            MemberCreateRequest request = new MemberCreateRequest("superAdmin", "최고관리자", "1234", AccountRole.SUPER_ADMIN, MemberType.GENERAL);
            memberStrategyFactory.getWriteService(AccountRole.SUPER_ADMIN).createMember(request);
        }
    }

    //    관리자가 없을경우 관리자 10개 생성
    @Transactional
    public void createMemberByRoleAdmin() {
        if (!memberStrategyFactory.getReadService(AccountRole.ADMIN).existsByRole(AccountRole.ADMIN)) {
            for (int i = 1; i <= 10; i++) {
                MemberCreateRequest request = new MemberCreateRequest("admin" + i, "관리자" + i, "1234", AccountRole.ADMIN, MemberType.GENERAL);
                memberStrategyFactory.getWriteService(AccountRole.ADMIN).createMember(request);
            }
        }
    }

    //    유저가 없을경우 유저 200개 생성
    @Transactional
    public void createMemberByRoleUser() {
        if (!memberStrategyFactory.getReadService(AccountRole.USER).existsByRole(AccountRole.USER)) {
            for (int i = 1; i <= 200; i++) {
                // USER 생성 시 Role은 USER로 지정 (MemberCreateRequest.fromUser 같은 팩토리 메서드 사용 가능하나 직접 생성)
                MemberCreateRequest request = new MemberCreateRequest("user" + i, "유저이름" + i, "1234", AccountRole.USER, MemberType.GENERAL);
                memberStrategyFactory.getWriteService(AccountRole.USER).createMember(request);
            }
        }
    }
}