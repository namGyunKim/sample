package gyun.sample.domain.member.repository.custom;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.customer.SearchCustomerForAdminRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> searchCustomerForAdmin(SearchCustomerForAdminRequest request, Pageable pageable);
}
