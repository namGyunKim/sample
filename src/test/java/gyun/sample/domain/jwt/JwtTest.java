package gyun.sample.domain.jwt;

import gyun.sample.domain.account.payload.response.TokenResponse;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.global.utils.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@SpringBootTest
@Transactional
public class JwtTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Test
    public void jwtTest() {
        //given
        SaveMemberForCustomerRequest request = new SaveMemberForCustomerRequest("test", "test", "test");
        Member member = new Member(request);

        //when
        String accessToken = jwtTokenProvider.createAccessToken(member);
        TokenResponse tokenResponse = jwtTokenProvider.getTokenResponse(accessToken);

        //then
        Assertions.assertThat(tokenResponse.loginId()).isEqualTo(member.getLoginId());
        System.out.println("tokenResponse = " + tokenResponse);

    }
}
