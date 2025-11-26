package gyun.sample.domain.member.service.read;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.dto.MemberListRequestDTO;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import gyun.sample.domain.member.payload.response.MemberListResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.specification.MemberSpecification;
import gyun.sample.global.enums.GlobalOrderEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadAdminService extends AbstractReadMemberService {

    protected final MemberRepository memberRepository;

    @Override
    public List<AccountRole> getSupportedRoles() {
        return List.of(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
    }

    @Override
    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    @Override
    public Page<MemberListResponse> getList(MemberListRequestDTO request) {
        // 1. 정렬 설정 (기본값: 생성일 내림차순)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (request.order() == GlobalOrderEnums.CREATE_ASC) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        }

        // 2. Pageable 생성
        Pageable pageable = PageRequest.of(request.page() - 1, request.size(), sort);

        // 3. 조회 대상 권한 (ADMIN, SUPER_ADMIN)
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);

        // 4. Specification 생성 및 조회
        Specification<Member> spec = MemberSpecification.searchMember(request, roles);
        Page<Member> memberList = memberRepository.findAll(spec, pageable);

        return memberList.map(MemberListResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = memberRepository.findByIdAndRoleIn(id, roles)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        return new DetailMemberResponse(member);
    }

    @Override
    public Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles) {
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        return member;
    }

    @Override
    public Member getByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
        return member;
    }
}