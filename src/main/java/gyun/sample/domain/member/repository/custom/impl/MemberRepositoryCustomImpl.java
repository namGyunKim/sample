package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.utils.MemberRepositoryCustomUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final MemberRepositoryCustomUtil memberRepositoryCustomUtil;

    QMember member = QMember.member;

    @Override
    public boolean existByLoginId(String loginId) {
        BooleanBuilder builder = memberRepositoryCustomUtil.existByLoginIdBuilder(loginId);
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

}
