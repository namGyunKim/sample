package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberUserUpdateValidator implements Validator {

    private final MemberRepository memberRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReadMemberService readUserService;

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
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(httpServletRequest);
        if (memberRepository.existsByNickNameAndLoginIdNot(request.nickName(), tokenResponse.loginId())) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }

        readUserService.getByLoginIdAndRole(tokenResponse.loginId(), AccountRole.USER);
    }
}
