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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static gyun.sample.global.utils.UtilService.getPageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadAdminService implements ReadMemberService {

    protected final MemberRepository memberRepository;

    @Override
    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    @Override
    public Page<MemberListResponse> getList(MemberListRequestDTO request) {
        Pageable pageable = getPageable(request.page(), request.size());
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Page<Member> memberList = memberRepository.getMemberList(request, roles, pageable);
        return memberList.map(MemberListResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = memberRepository.findByIdAndRoleIn(id, roles).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        return new DetailMemberResponse(member);
    }

    @Override
    public Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles) {
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        validationMember(member);
        return member;
    }

    @Override
    public Member getByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        validationMember(member);
        return member;
    }

    private void validationMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
    }
}
