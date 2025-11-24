package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.payload.request.MemberCreateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MemberCreateValidator implements Validator {

    private final MemberRepository memberRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberCreateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberCreateRequest request = (MemberCreateRequest) target;

        // URL Path Variable에서 Role 추출 (/api/member/{role}/create)
        Map<?, ?> pathVariables = (Map<?, ?>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String roleStr = (String) pathVariables.get("role");
        AccountRole pathRole = AccountRole.create(roleStr);

        // 1. 공통 중복 검사
        validateCommon(request, errors);

        // 2. 관리자 생성 요청인데 Request Body에 권한이 없는 경우 체크
        // (사용자 생성 요청일 때는 Request Body의 role이 null이어도 무관하므로 체크 안 함)
        if (pathRole == AccountRole.ADMIN || pathRole == AccountRole.SUPER_ADMIN) {
            if (request.role() == null) {
                errors.rejectValue("role", "role.empty", "관리자 생성 시 권한 값은 필수입니다.");
            }
        }
    }

    private void validateCommon(MemberCreateRequest request, Errors errors) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            errors.rejectValue("loginId", "loginId.duplicate", "이미 등록된 로그인 아이디입니다.");
        }

        if (memberRepository.existsByNickName(request.nickName())) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }
    }
}