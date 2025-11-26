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
 * JPA Specification을 이용한 동적 쿼리 생성
 */
public class MemberSpecification {

    public static Specification<Member> searchMember(MemberListRequestDTO request, List<AccountRole> roles) {
        return (Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 권한(Role) 필터링 (IN 절)
            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.get("role").in(roles));
            }

            // 2. 활성화 상태(Active) 필터링
            // ALL이 아닌 경우에만 WHERE 조건 추가
            if (request.active() != null && request.active() != GlobalActiveEnums.ALL) {
                predicates.add(builder.equal(root.get("active"), request.active()));
            }

            // 3. 검색어(SearchWord) 및 필터(Filter) 처리
            if (StringUtils.hasText(request.searchWord())) {
                String keyword = "%" + request.searchWord() + "%";
                GlobalFilterEnums filter = request.filter() != null ? request.filter() : GlobalFilterEnums.ALL;

                Predicate searchPredicate = switch (filter) {
                    case LOGIN_ID -> builder.like(root.get("loginId"), keyword);
                    case NICK_NAME -> builder.like(root.get("nickName"), keyword);
                    // ALL인 경우: 로그인 ID 또는 닉네임 중 하나라도 매칭되면 검색 (OR 조건)
                    default -> builder.or(
                            builder.like(root.get("loginId"), keyword),
                            builder.like(root.get("nickName"), keyword)
                    );
                };
                predicates.add(searchPredicate);
            }

            // 모든 조건을 AND로 결합하여 반환
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}