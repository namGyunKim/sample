package gyun.sample.domain.member.repository.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.payload.request.customer.SearchCustomerForAdminRequest;
import gyun.sample.domain.searchfilter.enums.ActiveType;
import org.springframework.stereotype.Component;

@Component
public class MemberRepositoryCustomUtil {

    QMember member = QMember.member;

    public BooleanBuilder searchCustomerForAdminBuilder(SearchCustomerForAdminRequest request) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        createActiveExpression(request.activeType(), booleanBuilder);

        return booleanBuilder;
    }

    private void createActiveExpression(ActiveType activeType, BooleanBuilder booleanBuilder) {
        if (!activeType.equals(ActiveType.ALL)) {
            if (activeType.equals(ActiveType.ACTIVE_TYPE)) {
                BooleanExpression activeExpression = member.active.eq(true);
                booleanBuilder.and(activeExpression);
            } else {
                BooleanExpression activeExpression = member.active.eq(false);
                booleanBuilder.and(activeExpression);
            }
        }

    }
}