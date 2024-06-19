package gyun.sample.domain.member.repository.custom.util;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRepositoryCustomUtil {

    protected final QMember member = QMember.member;


    public OrderSpecifier<?> getOrderSpecifierWithAdminMember(GlobalOrderEnums order) {
        return switch (order) {
            case CREATE_ASC -> new OrderSpecifier<>(Order.ASC, member.createdAt);
            case CREATE_DESC -> new OrderSpecifier<>(Order.DESC, member.createdAt);
        };
    }

    public void addSearchConditionsWithAdminMember(BooleanBuilder builder, GlobalFilterEnums filter, String searchWord) {
        switch (filter) {
            case NICK_NAME -> builder.and(member.nickName.containsIgnoreCase(searchWord));
            case LOGIN_ID -> builder.and(member.loginId.containsIgnoreCase(searchWord));
            case ALL -> {
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(member.nickName.containsIgnoreCase(searchWord));
                orBuilder.or(member.loginId.containsIgnoreCase(searchWord));
                builder.and(orBuilder);
            }
        }
    }
}