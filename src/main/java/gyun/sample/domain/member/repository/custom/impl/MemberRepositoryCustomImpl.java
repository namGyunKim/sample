package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QMember member = QMember.member;

    @Override
    public Page<Member> getMemberList(GetMemberListRequest getMemberListRequest, AccountRole accountRole, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.role.eq(accountRole));

        final String searchWord = getMemberListRequest.searchWord();
        if (!searchWord.isBlank()) {
            if (getMemberListRequest.filter().equals(GlobalFilterEnums.NICK_NAME)) {
                builder.and(member.nickName.containsIgnoreCase(searchWord));
            } else if (getMemberListRequest.filter().equals(GlobalFilterEnums.LOGIN_ID)) {
                builder.and(member.loginId.containsIgnoreCase(searchWord));
            } else {
                builder.and(member.nickName.containsIgnoreCase(searchWord));
                builder.and(member.loginId.containsIgnoreCase(searchWord));
            }
        }

        // 동적 정렬 추가
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(getMemberListRequest.order());

        List<Member> content = jpaQueryFactory.selectFrom(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        JPAQuery<Member> total = jpaQueryFactory.selectFrom(member)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, total::fetchCount);
    }

    private OrderSpecifier<?> getOrderSpecifier(GlobalOrderEnums order) {

        return switch (order) {
            case CREATE_ASC -> new OrderSpecifier<>(Order.ASC, member.createdAt);
            case CREATE_DESC -> new OrderSpecifier<>(Order.DESC, member.createdAt);
        };
    }
}
