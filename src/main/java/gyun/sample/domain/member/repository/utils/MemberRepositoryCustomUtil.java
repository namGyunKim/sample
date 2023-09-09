package gyun.sample.domain.member.repository.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.QMember;
import org.springframework.stereotype.Component;

@Component
public class MemberRepositoryCustomUtil {

    QMember member = QMember.member;

    //    권한으로 활성화 된 유저 존재여부 확인
    public BooleanBuilder existByRoleBuilder(AccountRole role) {
        BooleanExpression roleExpression = member.role.eq(role);
        BooleanExpression activeExpression = member.active.eq(true);
        return new BooleanBuilder().and(roleExpression.and(activeExpression));
    }
}