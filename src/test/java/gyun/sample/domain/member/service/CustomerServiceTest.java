package gyun.sample.domain.member.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.response.customer.InformationCustomerForSelfResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;
    CustomerValidator customerValidator;

    @Test
    void informationCustomerForSelf() {
        //given
        Member member = customerService.findByLoginIdAndRole("skarbs01", AccountRole.CUSTOMER);
        CurrentAccountDTO account = new CurrentAccountDTO(member.getLoginId(),member.getName(),member.getRole());
        //when
        InformationCustomerForSelfResponse informationCustomerForSelfResponse = new InformationCustomerForSelfResponse(member);
        //then
        assertEquals(member.getLoginId(),informationCustomerForSelfResponse.loginId());
    }
}