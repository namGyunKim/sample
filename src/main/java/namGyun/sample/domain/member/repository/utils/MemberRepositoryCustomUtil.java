package namGyun.sample.domain.member.repository.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import namGyun.sample.domain.member.entity.QMember;
import org.springframework.stereotype.Component;

@Component
public class MemberRepositoryCustomUtil {

    QMember member = QMember.member;

    public BooleanBuilder existByLoginIdBuilder(String loginId) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        return new BooleanBuilder().and(loginIdExpression);
    }
}
