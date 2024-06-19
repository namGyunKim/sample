package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.custom.util.MemberRepositoryCustomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final MemberRepositoryCustomUtil memberRepositoryCustomUtil;
    private final QMember member = QMember.member;

    @Override
    public Page<Member> getMemberList(GetMemberListRequest getMemberListRequest, AccountRole accountRole, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.role.eq(accountRole));

        final String searchWord = getMemberListRequest.searchWord();
        if (!searchWord.isBlank()) {
            memberRepositoryCustomUtil.addSearchConditionsWithAdminMember(builder, getMemberListRequest.filter(), searchWord);
        }

        // 동적 정렬 추가
        OrderSpecifier<?> orderSpecifier = memberRepositoryCustomUtil.getOrderSpecifierWithAdminMember(getMemberListRequest.order());

        List<Member> content = jpaQueryFactory.selectFrom(member)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Member> total = jpaQueryFactory.selectFrom(member)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, total::fetchCount);
    }
}
