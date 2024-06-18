package gyun.sample.domain.member.validator;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.payload.request.admin.CreateMemberRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CreateAdminValidator implements Validator {

    private final MemberRepository memberRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateMemberRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Authorization 헤더 검증 및 SUPER_ADMIN 권한 확인
        validateAuthorizationHeader();

        // CreateMemberRequest 검증
        CreateMemberRequest request = (CreateMemberRequest) target;
        validateMemberRequest(request, errors);
    }

    private void validateAuthorizationHeader() {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader == null) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED, "헤더가 비어있음");
        }
        String token = authorizationHeader.substring(7);
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(token);
        if (!"SUPER_ADMIN".equalsIgnoreCase(tokenResponse.role())) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED, "최고 관리자만 접근 가능합니다.");
        }
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
