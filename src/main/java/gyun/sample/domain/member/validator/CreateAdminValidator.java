package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.payload.request.CreateMemberRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CreateAdminValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateMemberRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // CreateMemberRequest 검증
        CreateMemberRequest request = (CreateMemberRequest) target;
        validateMemberRequest(request, errors);
    }


    private void validateMemberRequest(CreateMemberRequest request, Errors errors) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            errors.rejectValue("loginId", "loginId.duplicate", "이미 등록된 로그인 아이디입니다.");
        }

        if (memberRepository.existsByNickName(request.nickName())) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }
    }
}
