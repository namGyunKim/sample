package gyun.sample.domain.member.service.read;


import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.account.repository.RefreshTokenRepository;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.admin.AllMemberRequest;
import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import gyun.sample.domain.member.payload.response.admin.DetailMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.service.BaseMemberService;
import gyun.sample.domain.social.SocialServiceAdapter;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.error.enums.ErrorCode;
import gyun.sample.global.exception.GlobalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReadUserService extends BaseMemberService implements ReadMemberService {

    public ReadUserService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, SocialServiceAdapter socialServiceAdapter) {
        super(passwordEncoder, memberRepository, refreshTokenRepository, socialServiceAdapter);
    }

    @Override
    public boolean existsByRole(AccountRole accountRole) {
        return memberRepository.existsByRole(accountRole);
    }

    @Override
    public Page<AllMemberResponse> getList(AllMemberRequest request) {
        Pageable pageable = PageRequest.of(request.page() - 1, request.size());
        List<AccountRole> roles = List.of(AccountRole.USER);
        Page<Member> memberList = memberRepository.getMemberList(request, roles, pageable);
        return memberList.map(AllMemberResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        Member member = memberRepository.findByIdAndRole(id, AccountRole.USER).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        return new DetailMemberResponse(member);
    }
}
