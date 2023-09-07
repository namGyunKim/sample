package gyun.sample.domain.member.repository.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.QMember;
import org.springframework.stereotype.Component;

@Component
public class MemberRepositoryCustomUtil {

    QMember member = QMember.member;

    //    로그인 아이디로 활성화 및 비활성화 된 유저 존재여부 확인
    public BooleanBuilder existByLoginIdAndActiveAllBuilder(String loginId) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        return new BooleanBuilder().and(loginIdExpression);
    }

    //    로그인 아이디와 권환으로 활성화 된 유저 존재여부 확인
    public BooleanBuilder existByLoginIdAndRoleBuilder(String loginId,AccountRole role) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        BooleanExpression roleExpression = member.role.eq(role);
        return new BooleanBuilder().and(loginIdExpression.and(roleExpression));
    }

    //    권한으로 활성화 된 유저 존재여부 확인
    public BooleanBuilder existByRoleBuilder(AccountRole role) {
        BooleanExpression roleExpression = member.role.eq(role);
        BooleanExpression activeExpression = member.active.eq(true);
        return new BooleanBuilder().and(roleExpression.and(activeExpression));
    }

    //    로그인 아이디와 권한으로 활성화 된 유저 찾기
    public BooleanBuilder findByLoginIdAndRoleBuilder(String loginId, AccountRole role) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        BooleanExpression activeExpression = member.active.eq(true);
        BooleanExpression roleExpression = member.role.eq(role);
        return new BooleanBuilder().and(loginIdExpression.and(activeExpression.and(roleExpression)));
    }

    //    로그인 아이디로 활성화 된 유저 찾기
    public BooleanBuilder findByLoginIdBuilder(String loginId) {
        BooleanExpression loginIdExpression = member.loginId.eq(loginId);
        BooleanExpression activeExpression = member.active.eq(true);
        return new BooleanBuilder().and(loginIdExpression.and(activeExpression));
    }
}