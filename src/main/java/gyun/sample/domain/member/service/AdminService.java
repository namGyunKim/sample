package gyun.sample.domain.member.service;

import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.account.service.AccountService;
import gyun.sample.domain.account.validator.AccountValidator;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.utils.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService extends AccountService {

    public AdminService(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository, AccountValidator accountValidator, RefreshTokenRepository refreshTokenRepository) {
        super(jwtTokenProvider, memberRepository, accountValidator, refreshTokenRepository);
    }

}
