package gyun.sample.domain.board.validator;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.board.payload.request.CommentInactiveRequest;
import gyun.sample.domain.board.service.read.ReadCommentService;
import gyun.sample.global.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class InactiveCommentValidator implements Validator {

    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest httpServletRequest;
    private final ReadCommentService readCommentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentInactiveRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommentInactiveRequest request = (CommentInactiveRequest) target;
        validateRequest(request, errors);
    }

    private void validateRequest(CommentInactiveRequest request, Errors errors) {
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(httpServletRequest);
        AccountRole accountRole = AccountRole.getByName(tokenResponse.role());

        if (!hasPermissionToInactiveComment(request.commentId(), tokenResponse, accountRole)) {
            errors.rejectValue("commentId", "invalid.commentId", "댓글 비활성화 권한이 없습니다.");
        }

        if (isAdmin(accountRole) && isInactiveReasonInvalid(request.inactiveReason())) {
            errors.rejectValue("inactiveReason", "invalid.inactiveReason", "관리자의 경우 비활성화 사유를 입력해주세요.");
        }
    }

    private boolean hasPermissionToInactiveComment(long commentId, TokenResponse tokenResponse, AccountRole accountRole) {
        return readCommentService.isCommentOwner(commentId, tokenResponse.id()) || isAdmin(accountRole);
    }

    private boolean isAdmin(AccountRole accountRole) {
        return accountRole == AccountRole.ADMIN || accountRole == AccountRole.SUPER_ADMIN;
    }

    private boolean isInactiveReasonInvalid(String inactiveReason) {
        return inactiveReason == null || inactiveReason.isBlank();
    }
}