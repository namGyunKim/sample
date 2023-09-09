package gyun.sample.domain.member.api;


import gyun.sample.domain.member.service.AdminService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminRestController", description = "관리자 api")
@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminRestController {

    //    service
    private final AdminService adminService;
    //    utils
    protected final RestApiController restApiController;



}
