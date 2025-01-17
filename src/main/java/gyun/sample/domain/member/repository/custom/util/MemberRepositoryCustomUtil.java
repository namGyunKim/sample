package gyun.sample.domain.member.repository.custom.util;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberRepositoryCustomUtil {

    protected final QMember member = QMember.member;


    public OrderSpecifier<?> getMemberListOrder(GlobalOrderEnums order) {
        return switch (order) {
            case CREATE_ASC -> new OrderSpecifier<>(Order.ASC, member.createdAt);
            case CREATE_DESC -> new OrderSpecifier<>(Order.DESC, member.createdAt);
        };
    }

    public BooleanBuilder getMemberListFilter(MemberListRequestDTO request, List<AccountRole> accountRoles) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.role.in(accountRoles));
        GlobalActiveEnums active = request.active();
        if (active != GlobalActiveEnums.ALL) {
            builder.and(member.active.eq(active));
        }
        final String searchWord = request.searchWord();
        if (searchWord != null && !searchWord.isBlank()) {
            addMemberListSearchConditions(builder, request.filter(), searchWord);
        }

        return builder;
    }

    private void addMemberListSearchConditions(BooleanBuilder builder, GlobalFilterEnums filter, String searchWord) {
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