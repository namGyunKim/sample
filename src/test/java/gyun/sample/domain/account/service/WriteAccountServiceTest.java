package gyun.sample.domain.account.service;

import gyun.sample.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WriteAccountServiceTest {

    @Autowired
    private WriteAccountService writeAccountService;



    @Test
    void findMember() {
        //given
        Member Admin = writeAccountService.findByLoginId("superAdmin");
        //when
        //then
        assertEquals(Admin.getLoginId(), "superAdmin");

    }
}