package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.utils.MemberRepositoryCustomUtil;

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
}
