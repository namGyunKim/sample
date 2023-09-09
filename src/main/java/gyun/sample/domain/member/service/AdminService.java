package gyun.sample.domain.member.service;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.response.InformationCustomerForAdminResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.validator.AdminValidator;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService extends AccountService {

    // validator
    private final AdminValidator adminValidator;

    public AdminService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AccountValidator accountValidator, JwtTokenProvider jwtTokenProvider, AdminValidator adminValidator) {
        super(memberRepository, refreshTokenRepository, accountValidator, jwtTokenProvider);
        this.adminValidator = adminValidator;
    }


}