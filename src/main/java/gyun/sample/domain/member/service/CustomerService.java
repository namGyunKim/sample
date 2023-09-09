package gyun.sample.domain.member.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.response.InformationCustomerForAdminResponse;
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

    //  관리자 전용 고객 조회
    public InformationCustomerForAdminResponse informationCustomerForAdmin(CurrentAccountDTO account, String loginId) {
        Member member = findByLoginId(loginId);
        customerValidator.informationCustomerForAdmin(account,member);
        return new InformationCustomerForAdminResponse(member);
    }

    //  고객이 자신의 정보 조회
//    public InformationCustomerForAdminResponse informationCustomerForSelf(CurrentAccountDTO account) {
//        Member member = findMemberByLoginIdAndRole(account.loginId(), AccountRole.CUSTOMER);
//        return new InformationCustomerForAdminResponse(member);
//    }
}
