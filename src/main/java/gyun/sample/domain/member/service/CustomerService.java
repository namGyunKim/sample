package gyun.sample.domain.member.service;

import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.response.SaveMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.validator.CustomerValidator;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerService extends AccountService {

    // validator
    private final CustomerValidator customerValidator;

    public CustomerService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AccountValidator accountValidator, JwtTokenProvider jwtTokenProvider, CustomerValidator customerValidator) {
        super(memberRepository, refreshTokenRepository, accountValidator, jwtTokenProvider);
        this.customerValidator = customerValidator;
    }

    //  고객 회원가입
    @Transactional
    public SaveMemberResponse saveCustomer(SaveMemberForCustomerRequest request){
        customerValidator.validateSaveCustomer(request);
        Member member = new Member(request);
        saveMember(member);
        return new SaveMemberResponse(member);
    }
}
