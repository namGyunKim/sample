package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.global.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAdminUpdateValidator implements Validator {

    private final MemberRepository memberRepository;
    private final ReadMemberService readAdminService;
    private final UtilService utilService;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        MemberUpdateRequest request = (MemberUpdateRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(MemberUpdateRequest request, Errors errors) {
        CurrentAccountDTO currentAccount = utilService.getCurrentAccount();
        if (memberRepository.existsByNickNameAndLoginIdNot(request.nickName(), currentAccount.loginId())) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }

        List<AccountRole> roles = List.of(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        readAdminService.getByLoginIdAndRoles(currentAccount.loginId(), roles);
    }
}
