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
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static gyun.sample.global.utils.UtilService.getPageable;

@Service
@Transactional(readOnly = true)
public class ReadAdminService extends BaseMemberService implements ReadMemberService {

    public ReadAdminService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, SocialServiceAdapter socialServiceAdapter) {
        super(passwordEncoder, memberRepository, refreshTokenRepository, socialServiceAdapter);
    }


    @Override
    public Page<AllMemberResponse> getList(AllMemberRequest request) {
        Pageable pageable = getPageable(request.page(), request.size());
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Page<Member> memberList = memberRepository.getMemberList(request, roles, pageable);
        return memberList.map(AllMemberResponse::new);
    }

    @Override
    public DetailMemberResponse getDetail(long id) {
        List<AccountRole> roles = Arrays.asList(AccountRole.ADMIN, AccountRole.SUPER_ADMIN);
        Member member = memberRepository.findByIdAndRoleIn(id, roles).orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_MEMBER));
        if (member.getActive() == GlobalActiveEnums.INACTIVE) throw new GlobalException(ErrorCode.INACTIVE_MEMBER);
        return new DetailMemberResponse(member);
    }
}
