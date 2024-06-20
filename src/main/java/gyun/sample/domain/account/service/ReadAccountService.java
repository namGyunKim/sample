package gyun.sample.domain.account.service;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.account.payload.response.LoginMemberResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReadAccountService extends BaseAccountService {

    public ReadAccountService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    public LoginMemberResponse getLoginData(CurrentAccountDTO request) {
        Member byLoginIdAndRole = memberRepository.findByLoginIdAndRole(request.loginId(), request.role()).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (byLoginIdAndRole.getActive() != GlobalActiveEnums.ACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        return new LoginMemberResponse(byLoginIdAndRole);
    }

}
