package gyun.sample.domain.account.payload.dto;

import gyun.sample.global.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;

public record ClaimsWithErrorCodeDTO(
        Claims claims,
        ErrorCode errorCode
) {
}
