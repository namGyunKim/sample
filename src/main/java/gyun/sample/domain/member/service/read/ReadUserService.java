package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import gyun.sample.domain.member.payload.response.MemberListResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gyun.sample.global.utils.UtilService.getPageable;

/**
 * 일반 사용자(USER) 정보 조회 서비스
 * - 관리자는 탈퇴한 회원도 조회 가능
 * - 일반 사용자는 활성(ACTIVE) 상태인 회원만 조회 가능
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
        Pageable pageable = getPageable(request.page(), request.size());
        // USER 권한을 가진 회원만 조회
        List<AccountRole> roles = List.of(AccountRole.USER);

        // QueryDSL을 사용하여 필터링 및 페이징 처리
        Page<Member> memberList = memberRepository.getMemberList(request, roles, pageable);

        return memberList.map(MemberListResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        // 1. ID와 Role로 회원 조회 (없으면 예외 발생)
        Member member = memberRepository.findByIdAndRole(id, AccountRole.USER)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        // 2. 접근 권한 및 상태 체크 (관리자는 탈퇴 회원 조회 가능)
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

    // 회원이 활성 상태인지 검증
    private void validationActiveMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) {
            throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        }
    }

    // 현재 로그인한 사용자가 관리자(ADMIN, SUPER_ADMIN)인지 확인
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            AccountRole role = principal.getRole();
            return role == AccountRole.ADMIN || role == AccountRole.SUPER_ADMIN;
        }

        return false;
    }
}