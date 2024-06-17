package gyun.sample.domain.account.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.SaveMemberWithSuperAdminRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WriteAccountServiceTest {

    @Autowired
    private WriteAccountService writeAccountService;


    @Test
    void saveMember() {
        //given
        SaveMemberWithSuperAdminRequest request = new SaveMemberWithSuperAdminRequest("superAdminTest", "최고관리자Test", "1234");
        Member member = new Member(request);
        //when
        writeAccountService.saveMember(member);
        Member findMember = writeAccountService.findByLoginId("superAdminTest");
        //then
        assertEquals(member.getId(), findMember.getId());

    }

    @Test
    void findMember() {
        //given
        Member Admin = writeAccountService.findByLoginId("superAdmin");
        //when
        //then
        assertEquals(Admin.getLoginId(), "superAdmin");

    }
}