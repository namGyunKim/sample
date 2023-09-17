package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.payload.request.customer.SearchCustomerForAdminRequest;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.utils.MemberRepositoryCustomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    //    querydsl
    private final JPAQueryFactory jpaQueryFactory;
    //    querydsl util
    private final MemberRepositoryCustomUtil memberRepositoryCustomUtil;

    QMember member = QMember.member;

//    관리자를 위한 고객 최신순 조회
    @Override
    public Page<Member> searchCustomerForAdmin(SearchCustomerForAdminRequest request, Pageable pageable) {
        BooleanBuilder booleanBuilder =memberRepositoryCustomUtil.searchCustomerForAdminBuilder(request);

        JPAQuery<Member> total = jpaQueryFactory
                .selectFrom(member)
                .where(booleanBuilder);

        List<Member> content = jpaQueryFactory
                .selectFrom(member)
                .where(booleanBuilder)
                .orderBy(member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> total.fetch().size());
    }
}
