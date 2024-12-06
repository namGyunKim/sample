package gyun.sample.domain.member.service.read;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.AllMemberRequest;
import gyun.sample.domain.member.payload.response.AllMemberResponse;
import gyun.sample.domain.member.payload.response.DetailMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gyun.sample.global.utils.UtilService.getPageable;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadUserService implements ReadMemberService {

    protected final PasswordEncoder passwordEncoder;
    protected final MemberRepository memberRepository;
    protected final RefreshTokenRepository refreshTokenRepository;
    protected final SocialServiceAdapter socialServiceAdapter;

    @Override
    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    @Override
    public Page<AllMemberResponse> getList(AllMemberRequest request) {
        Pageable pageable = getPageable(request.page(), request.size());
        List<AccountRole> roles = List.of(AccountRole.USER);
        Page<Member> memberList = memberRepository.getMemberList(request, roles, pageable);

        return memberList.map(AllMemberResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        Member member = memberRepository.findByIdAndRole(id, AccountRole.USER).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        validationMember(member);
        return new DetailMemberResponse(member);
    }

    @Override
    public Member getByLoginIdAndRoles(String loginId, List<AccountRole> roles) {
        Member member = memberRepository.findByLoginIdAndRoleIn(loginId, roles).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        validationMember(member);
        return member;
    }

    @Override
    public Member getByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        validationMember(member);
        return member;
    }

    private void validationMember(Member member) {
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
    }
}
