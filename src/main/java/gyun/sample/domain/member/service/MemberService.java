package gyun.sample.domain.member.service;

import lombok.RequiredArgsConstructor;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberRequest;
import gyun.sample.domain.member.payload.response.SaveMemberResponse;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.member.validator.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository userRepository;
    private final UserValidator userValidator;

    @Transactional
    public SaveMemberResponse saveUser(SaveMemberRequest request){
        userValidator.validateSaveUser(request);
        Member member = new Member(request);
        userRepository.save(member);
        return new SaveMemberResponse(member);
    }
}
