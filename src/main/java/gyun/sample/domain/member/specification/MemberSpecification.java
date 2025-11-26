package gyun.sample.domain.member.specification;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Criteria API를 래핑한 Specification
 * QueryDSL 없이 동적 쿼리 생성
 */
public class MemberSpecification {

    public static Specification<Member> searchMember(MemberListRequestDTO request, List<AccountRole> roles) {
        return (Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Role 필터링
            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.get("role").in(roles));
            }

            // 2. Active 상태 필터링
            if (request.active() != null && request.active() != GlobalActiveEnums.ALL) {
                predicates.add(builder.equal(root.get("active"), request.active()));
            }

            // 3. 검색어(SearchWord) 필터링
            if (StringUtils.hasText(request.searchWord())) {
                String keyword = "%" + request.searchWord() + "%";
                GlobalFilterEnums filter = request.filter() != null ? request.filter() : GlobalFilterEnums.ALL;

                Predicate searchPredicate = switch (filter) {
                    case LOGIN_ID -> builder.like(root.get("loginId"), keyword);
                    case NICK_NAME -> builder.like(root.get("nickName"), keyword);
                    // ALL인 경우 LoginId 또는 NickName에서 검색
                    default -> builder.or(
                            builder.like(root.get("loginId"), keyword),
                            builder.like(root.get("nickName"), keyword)
                    );
                };
                predicates.add(searchPredicate);
            }

            // WHERE 절 생성
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}