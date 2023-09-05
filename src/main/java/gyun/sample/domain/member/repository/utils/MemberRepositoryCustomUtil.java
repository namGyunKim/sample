package gyun.sample.domain.member.repository.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.QMember;
import org.springframework.stereotype.Component;

@Component
public class MemberRepositoryCustomUtil {

    QMember member = QMember.member;

    public BooleanBuilder existByLoginIdAndActiveAllBuilder(String loginId) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        return new BooleanBuilder().and(loginIdExpression);
    }

    public BooleanBuilder existByRoleBuilder(AccountRole role) {
        BooleanExpression roleExpression = member.role.eq(role);
        BooleanExpression activeExpression = member.active.eq(true);
        return new BooleanBuilder().and(roleExpression.and(activeExpression));
    }

    public BooleanBuilder findByLoginIdAndRoleBuilder(String loginId, AccountRole role) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        BooleanExpression activeExpression = member.active.eq(true);
        BooleanExpression roleExpression = member.role.eq(role);
        return new BooleanBuilder().and(loginIdExpression.and(activeExpression.and(roleExpression)));
    }

    public BooleanBuilder findByLoginIdBuilder(String loginId) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        BooleanExpression activeExpression = member.active.eq(true);
        return new BooleanBuilder().and(loginIdExpression.and(activeExpression));
    }
}
