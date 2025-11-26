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
 * 일반 사용자(USER) 정보 조회 서비스
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

        // 2. Pageable 생성
        Pageable pageable = PageRequest.of(request.page() - 1, request.size(), sort);

        // 3. 조회 대상 권한 (USER)
        List<AccountRole> roles = List.of(AccountRole.USER);

        // 4. Specification 생성 및 조회
        Specification<Member> spec = MemberSpecification.searchMember(request, roles);
        Page<Member> memberList = memberRepository.findAll(spec, pageable);

        return memberList.map(MemberListResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        Member member = memberRepository.findByIdAndRole(id, AccountRole.USER)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        // 관리자가 아니면 활성 상태 체크
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

    private void validationActiveMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) {
            throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            AccountRole role = principal.getRole();
            return role == AccountRole.ADMIN || role == AccountRole.SUPER_ADMIN;
        }
        return false;
    }
}