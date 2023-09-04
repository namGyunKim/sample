package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.utils.MemberRepositoryCustomUtil;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final MemberRepositoryCustomUtil memberRepositoryCustomUtil;

    QMember member = QMember.member;

    @Override
    public boolean existByLoginIdAndActiveAll(String loginId) {
        BooleanBuilder builder = memberRepositoryCustomUtil.existByLoginIdAndActiveAllBuilder(loginId);
        return !jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetch().isEmpty();
    }

    @Override
    public boolean existByRoleSuperAdmin() {
        BooleanBuilder builder = memberRepositoryCustomUtil.existByRoleSuperAdminBuilder();
        return !jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetch().isEmpty();
    }

    @Override
    public Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role) {
        BooleanBuilder builder = memberRepositoryCustomUtil.findByLoginIdAndRoleBuilder(loginId,role);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetchOne());
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        BooleanBuilder builder = memberRepositoryCustomUtil.findByLoginIdBuilder(loginId);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetchOne());
    }
}
