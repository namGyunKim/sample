package namGyun.sample.domain.member.repository.custom;

public interface MemberRepositoryCustom {
    boolean existByLoginId(String loginId);
}
