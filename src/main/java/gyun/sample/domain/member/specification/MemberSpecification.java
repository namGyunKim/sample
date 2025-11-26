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
 * 회원 관련 동적 쿼리 생성 Specification
 * QueryDSL 대신 JPA Criteria API 사용
 */
public class MemberSpecification {

    /**
     * 검색 조건(DTO)과 역할 목록(Roles)을 받아 Specification 생성
     */
    public static Specification<Member> searchMember(MemberListRequestDTO request, List<AccountRole> roles) {
        return (Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Role 필터링 (IN 절)
            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.get("role").in(roles));
            }

            // 2. 활성 상태 필터링 (ALL이 아니면 해당 상태만 조회)
            if (request.active() != null && request.active() != GlobalActiveEnums.ALL) {
                predicates.add(builder.equal(root.get("active"), request.active()));
            }

            // 3. 검색어 필터링
            if (StringUtils.hasText(request.searchWord())) {
                String keyword = "%" + request.searchWord() + "%";
                GlobalFilterEnums filter = request.filter() != null ? request.filter() : GlobalFilterEnums.ALL;

                Predicate searchPredicate = switch (filter) {
                    case LOGIN_ID -> builder.like(root.get("loginId"), keyword);
                    case NICK_NAME -> builder.like(root.get("nickName"), keyword);
                    default -> builder.or(
                            builder.like(root.get("loginId"), keyword),
                            builder.like(root.get("nickName"), keyword)
                    );
                };
                predicates.add(searchPredicate);
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}