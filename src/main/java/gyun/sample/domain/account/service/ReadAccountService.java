package gyun.sample.domain.account.service;

import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadAccountService {

    private final MemberRepository memberRepository;

    public Member findByLoginIdAndRole(String loginId, AccountRole role) {
        Member member = memberRepository.findByLoginIdAndRole(loginId, role)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        }
        return member;
    }

    public Member findByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));

        if (member.getActive() != GlobalActiveEnums.ACTIVE) {
            throw new GlobalException(ErrorCode.MEMBER_INACTIVE);
        }
        return member;
    }
}