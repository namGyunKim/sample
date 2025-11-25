package gyun.sample.domain.member.validator;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.MemberUpdateRequest;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberUserUpdateValidator implements Validator {

    private final MemberRepository memberRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberUpdateRequest request = (MemberUpdateRequest) target;
        validateMemberRequest(request, errors);
    }

    private void validateMemberRequest(MemberUpdateRequest request, Errors errors) {
        // URL Path Variable에서 대상 ID 추출 (/member/{role}/update/{id})
        Map<?, ?> pathVariables = (Map<?, ?>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String idStr = (String) pathVariables.get("id");
        if (idStr == null) {
            // ID가 없는 경우 (혹시 모를 예외 상황)
            errors.reject("id.required", "대상 회원 ID가 없습니다.");
            return;
        }

        Long targetId;
        try {
            targetId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            errors.reject("id.invalid", "유효하지 않은 회원 ID입니다.");
            return;
        }

        // 대상 회원 조회 (존재 여부 확인)
        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        // 닉네임 중복 검사 (수정 대상 회원의 본인 닉네임은 제외하고 검사해야 함)
        if (memberRepository.existsByNickNameAndLoginIdNot(request.nickName(), targetMember.getLoginId())) {
            errors.rejectValue("nickName", "nickName.duplicate", "이미 등록된 닉네임입니다.");
        }

        // [삭제됨] 기존 로직: readUserService.getByLoginIdAndRole(currentLoginId, AccountRole.USER);
        // 이유: 관리자가 유저를 수정할 때, 관리자는 USER 권한이 없으므로 이 검증에서 실패함.
        // 권한 체크(@PreAuthorize)와 타겟 존재 여부 확인으로 충분함.
    }
}