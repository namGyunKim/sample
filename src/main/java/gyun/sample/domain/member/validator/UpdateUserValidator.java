package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.payload.request.UpdateMemberRequest;
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
public class UpdateUserValidator implements Validator {

    private final MemberRepository memberRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReadMemberService readUserService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateMemberRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        UpdateMemberRequest request = (UpdateMemberRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(UpdateMemberRequest request, Errors errors) {
        String bearer = httpServletRequest.getHeader("Authorization").split(" ")[1];
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(bearer);
        if (memberRepository.existsByNickNameAndLoginIdNot(request.nickName(), tokenResponse.loginId())) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }

        readUserService.getByLoginIdAndRole(tokenResponse.loginId(), AccountRole.USER);
    }
}
