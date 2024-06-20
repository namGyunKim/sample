package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.payload.request.admin.AllMemberRequest;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.custom.util.MemberRepositoryCustomUtil;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
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
    public Page<Member> getMemberList(AllMemberRequest request, List<AccountRole> accountRoles, Pageable pageable) {

//        조건 추가
        BooleanBuilder builder = memberRepositoryCustomUtil.getMemberListFilter(request, accountRoles);

        // 동적 정렬 추가
        OrderSpecifier<?> orderSpecifier = memberRepositoryCustomUtil.getMemberListOrder(request.order());

        List<Member> content = jpaQueryFactory.selectFrom(member)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(member.id.count())
                .from(member)
                .where(builder)
                .fetchOne();

        if (total == null) {
            throw new GlobalException(ErrorCode.COUNT_FETCH_ERROR, "관리자 수 조회에 실패하였습니다.");
        }

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }
}
