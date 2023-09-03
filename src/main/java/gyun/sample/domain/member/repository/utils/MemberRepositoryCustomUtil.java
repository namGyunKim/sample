package gyun.sample.domain.member.repository.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.QMember;
import org.springframework.stereotype.Component;

@Component
public class MemberRepositoryCustomUtil {

    QMember member = QMember.member;

    public BooleanBuilder existByLoginIdBuilder(String loginId) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        return new BooleanBuilder().and(loginIdExpression);
    }

    public BooleanBuilder existByRoleSuperAdminBuilder() {
        BooleanExpression superAdminExpression = member.role.eq(AccountRole.SUPER_ADMIN);
        return new BooleanBuilder().and(superAdminExpression);
    }
}
