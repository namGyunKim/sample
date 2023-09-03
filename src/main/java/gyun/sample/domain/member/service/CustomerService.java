package gyun.sample.domain.member.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.response.SaveMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.validator.CustomerValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerService extends MemberService{

    private final CustomerValidator customerValidator;

    public CustomerService(MemberRepository memberRepository, CustomerValidator customerValidator) {
        super(memberRepository);
        this.customerValidator = customerValidator;
    }

    @Transactional
    public SaveMemberResponse saveCustomer(SaveMemberForCustomerRequest request){
        customerValidator.validateSaveCustomer(request);
        Member member = new Member(request);
        saveMemberByRole(member);
        return new SaveMemberResponse(member);
    }


}
