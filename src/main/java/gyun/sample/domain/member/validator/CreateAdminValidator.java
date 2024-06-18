package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
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
        return clazz.isAssignableFrom(CreateMemberRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateMemberRequest request = (CreateMemberRequest) target;
        // 중복 체크
        boolean existsByLoginId = memberRepository.existsByLoginId(request.loginId());
        boolean existsByNickName = memberRepository.existsByNickName(request.nickName());
        if (existsByLoginId) {
            errors.rejectValue("loginId", "loginId.duplicate", "이미 등록된 로그인 아이디입니다.");
        }

        if (existsByNickName) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }
    }
}
