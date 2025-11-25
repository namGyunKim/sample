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
        List<AccountRole> roles = List.of(AccountRole.USER);
        Page<Member> memberList = memberRepository.getMemberList(request, roles, pageable);

        return memberList.map(MemberListResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        Member member = memberRepository.findByIdAndRole(id, AccountRole.USER).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        // [수정] 관리자는 탈퇴 회원 조회 가능, 일반 유저는 불가능
        if (!isAdmin()) {
            validationMember(member);
        }
        return new DetailMemberResponse(member);
    }

    @Override
    public Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles) {
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        // [수정] 관리자는 탈퇴 회원 조회 가능, 일반 유저는 불가능
        if (!isAdmin()) {
            validationMember(member);
        }
        return member;
    }

    @Override
    public Member getByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        // [수정] 관리자는 탈퇴 회원 조회 가능, 일반 유저는 불가능
        if (!isAdmin()) {
            validationMember(member);
        }
        return member;
    }

    private void validationMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
    }

    // 현재 로그인한 사용자가 관리자(ADMIN, SUPER_ADMIN)인지 확인
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails principal) {
            return principal.getRole() == AccountRole.ADMIN || principal.getRole() == AccountRole.SUPER_ADMIN;
        }
        return false;
    }
}