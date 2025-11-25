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
 * 회원 관련 동적 쿼리를 생성하기 위한 Specification 클래스입니다.
 * JPA Criteria API를 사용하여 복잡한 검색 조건을 처리합니다.
 */
public class MemberSpecification {

    /**
     * 회원 목록 조회를 위한 동적 쿼리 사양(Specification)을 생성합니다.
     *
     * @param request 검색 조건이 담긴 DTO (검색어, 검색 타입 등)
     * @param roles   필터링할 권한 목록
     * @return Specification<Member>
     */
    public static Specification<Member> getMemberListSpec(MemberListRequestDTO request, List<AccountRole> roles) {
        return (Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 권한 필터링 (roles 리스트가 비어있지 않은 경우에만 적용)
            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.get("role").in(roles));
            }

            // 2. 활성 상태 필터링 (ALL이 아닌 경우에만 적용)
            // 수정: request.active()는 GlobalActiveEnums 타입이므로, GlobalActiveEnums.ALL과 비교해야 합니다.
            if (request.active() != null && request.active() != GlobalActiveEnums.ALL) {
                predicates.add(builder.equal(root.get("active"), request.active()));
            }

            // 3. 검색어 및 필터링 적용
            // MemberListRequestDTO의 필드 접근자는 레코드의 기본 접근자(searchWord(), filter())를 사용합니다.
            if (StringUtils.hasText(request.searchWord())) {
                String keyword = "%" + request.searchWord() + "%";
                GlobalFilterEnums filter = request.filter();

                // 검색 필터에 따른 동적 조건 적용
                Predicate searchPredicate = switch (filter) {
                    case LOGIN_ID -> builder.like(root.get("loginId"), keyword);
                    case NICK_NAME -> builder.like(root.get("nickName"), keyword);
                    // ALL이거나 지원하지 않는 필터인 경우, OR 조건으로 처리
                    default -> builder.or(
                            builder.like(root.get("loginId"), keyword),
                            builder.like(root.get("nickName"), keyword)
                    );
                };
                predicates.add(searchPredicate);
            }

            // 최종적으로 모든 조건을 AND로 결합하여 반환
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}