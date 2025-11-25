package gyun.sample.domain.log.event;

import gyun.sample.domain.log.enums.LogType;

public record MemberActivityEvent(
        String loginId,
        Long memberId,
        LogType logType,
        String details,
        String clientIp
) {
    public static MemberActivityEvent of(String loginId, Long memberId, LogType logType, String details, String clientIp) {
        return new MemberActivityEvent(loginId, memberId, logType, details, clientIp);
    }
}