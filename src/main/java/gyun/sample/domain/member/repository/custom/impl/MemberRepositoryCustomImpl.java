package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    //    querydsl
    private final JPAQueryFactory jpaQueryFactory;

    QMember member = QMember.member;

}
