package gyun.sample.domain.member.api;


import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MemberRestController", description = "유저 api 컨트롤러")
@RestController(value = "MemberRestController")
//@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
@RequestMapping(value = "/api/member")
@RequiredArgsConstructor
//@SecurityRequirement(name = "Bearer Authentication")
public class MemberRestController{

    protected final RestApiController restApiController;

}
