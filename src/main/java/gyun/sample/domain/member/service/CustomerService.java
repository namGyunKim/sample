package gyun.sample.domain.member.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.customer.SaveCustomerForSelfRequest;
import gyun.sample.domain.member.payload.request.customer.UpdateCustomerForSelfRequest;
import gyun.sample.domain.member.payload.response.admin.InformationCustomerForAdminResponse;
import gyun.sample.domain.member.payload.response.customer.InformationCustomerForSelfResponse;
import gyun.sample.domain.member.payload.response.customer.SaveCustomerForSelfResponse;
import gyun.sample.domain.member.payload.response.customer.UpdateCustomerForSelfResponse;
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
    public SaveCustomerForSelfResponse saveCustomer(SaveCustomerForSelfRequest request){
        customerValidator.validateSaveCustomer(request);
        Member member = new Member(request);
        saveMember(member);
        return new SaveCustomerForSelfResponse(member);
    }

    //  관리자 전용 고객 조회
    public InformationCustomerForAdminResponse informationCustomerForAdmin(CurrentAccountDTO account, String loginId) {
        customerValidator.informationCustomerForAdmin(account);
        Member member = findByLoginIdAndRole(loginId, AccountRole.CUSTOMER);
        return new InformationCustomerForAdminResponse(member);
    }

    //  고객이 자신의 정보 조회
    public InformationCustomerForSelfResponse informationCustomerForSelf(CurrentAccountDTO account) {
        customerValidator.informationCustomerForSelf(account);
        Member member = findByLoginIdAndRole(account.loginId(), AccountRole.CUSTOMER);
        return new InformationCustomerForSelfResponse(member);
    }

    //  고객이 자신의 정보 수정
    @Transactional
    public UpdateCustomerForSelfResponse updateCustomerForSelf(CurrentAccountDTO account, UpdateCustomerForSelfRequest request) {
        customerValidator.updateCustomerForSelf(request);
        Member member = findByLoginIdAndRole(account.loginId(), AccountRole.CUSTOMER);
        member.update(request);
        return new UpdateCustomerForSelfResponse(member);
    }

    //  고객 탈퇴
    @Transactional
    public void deactivateCustomerForSelf(CurrentAccountDTO account) {
        customerValidator.deactivateCustomerForSelf(account);
        deactivateMember(account.loginId());
    }
}
