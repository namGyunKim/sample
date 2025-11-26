package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import gyun.sample.domain.member.payload.response.MemberListResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.specification.MemberSpecification;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 읽기 전용 서비스 (CQRS - Read)
 * Transactional(readOnly = true)를 통해 성능 최적화 (Dirty Checking 스냅샷 생성 안함)
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadUserService extends AbstractReadMemberService {

    protected final MemberRepository memberRepository;

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.USER);
    }

    @Override
    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    @Override
    public Page<MemberListResponse> getList(MemberListRequestDTO request) {
        // 1. 정렬 기준 설정 (기본값: 생성일 내림차순)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (request.order() == GlobalOrderEnums.CREATE_ASC) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        }

        // 2. Pageable 생성 (0-based index 주의)
        Pageable pageable = PageRequest.of(request.page() - 1, request.size(), sort);

        // 3. 조회 대상 권한 설정 (USER)
        List<AccountRole> roles = List.of(AccountRole.USER);

        // 4. Specification을 이용한 동적 쿼리 실행
        Specification<Member> spec = MemberSpecification.searchMember(request, roles);
        Page<Member> memberPage = memberRepository.findAll(spec, pageable);

        // 5. DTO 변환 후 반환
        return memberPage.map(MemberListResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        Member member = memberRepository.findByIdAndRole(id, AccountRole.USER)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        // 관리자가 조회하는 것이 아니라면, 비활성 회원은 조회 불가 처리
        if (!isAdmin()) {
            validationActiveMember(member);
        }

        return new DetailMemberResponse(member);
    }

    @Override
    public Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles) {
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        if (!isAdmin()) {
            validationActiveMember(member);
        }
        return member;
    }

    @Override
    public Member getByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        if (!isAdmin()) {
            validationActiveMember(member);
        }
        return member;
    }

    // 비활성 회원 검증 로직
    private void validationActiveMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) {
            throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        }
    }

    // 현재 사용자가 관리자(ADMIN, SUPER_ADMIN)인지 확인
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            AccountRole role = principal.getRole();
            return role == AccountRole.ADMIN || role == AccountRole.SUPER_ADMIN;
        }
        return false;
    }
}