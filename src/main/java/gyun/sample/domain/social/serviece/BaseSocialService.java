package gyun.sample.domain.social.serviece;

import gyun.sample.domain.account.service.WriteAccountService;
import gyun.sample.domain.member.service.read.ReadMemberService;
import gyun.sample.domain.member.service.write.WriteMemberService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseSocialService {

    protected final ReadMemberService readUserService;
    protected final WriteMemberService writeUserService;
    protected final WriteAccountService writeAccountService;

}
