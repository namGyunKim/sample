package gyun.sample.domain.member.api;


import gyun.sample.domain.member.service.AdminService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MemberRestController", description = "멤버 api")
@RestController
//@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
@RequestMapping(value = "/api/member")
@RequiredArgsConstructor
//@SecurityRequirement(name = "Bearer Authentication")
public class AdminRestController {

    private final AdminService adminService;

    protected final RestApiController restApiController;

}
