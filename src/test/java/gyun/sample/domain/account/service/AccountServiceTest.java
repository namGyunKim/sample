package gyun.sample.domain.account.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForSuperAdminRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;


    @Test
    void saveMember() {
        //given
        SaveMemberForSuperAdminRequest request = new SaveMemberForSuperAdminRequest("superAdminTest", "최고관리자Test", "1234");
        Member member = new Member(request);
        //when
        accountService.saveMember(member);
        Member findMember = accountService.findByLoginId("superAdminTest");
        //then
        assertEquals(member.getId(), findMember.getId());

    }

    @Test
    void findMember() {
        //given
        Member Admin = accountService.findByLoginId("superAdmin");
        //when
        //then
        assertEquals(Admin.getLoginId(), "superAdmin");

    }
}