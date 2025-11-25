package gyun.sample.domain.log.payload.response;

import gyun.sample.domain.log.entity.MemberLog;
import gyun.sample.domain.log.enums.LogType;
import gyun.sample.global.utils.UtilService;

public record MemberLogResponse(
        Long id,
        String loginId,     // 대상 회원 ID
        String executorId,  // 수행자 ID
        LogType logType,    // 로그 유형
        String details,     // 상세 내용
        String clientIp,    // IP 주소
        String createdAt    // 생성일시
) {
    public MemberLogResponse(MemberLog log) {
        this(
                log.getId(),
                log.getLoginId(),
                log.getExecutorId(),
                log.getLogType(),
                log.getDetails(),
                log.getClientIp(),
                UtilService.formattedTime(log.getCreatedAt())
        );
    }
}