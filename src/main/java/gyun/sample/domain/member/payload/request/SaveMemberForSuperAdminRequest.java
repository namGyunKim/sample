package gyun.sample.domain.member.payload.request;

public record SaveMemberForSuperAdminRequest(
        String loginId,
        String name,
        String password
) {
}
