package gyun.sample.domain.account.dto;

import gyun.sample.global.error.enums.ErrorCode;
import io.jsonwebtoken.Claims;

public record ClaimsWithErrorCodeDTO(
        Claims claims,
        ErrorCode errorCode
) {
}
