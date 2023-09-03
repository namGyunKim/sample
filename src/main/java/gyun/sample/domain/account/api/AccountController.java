package gyun.sample.domain.account.api;

import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: 2023/09/04 JWT 인증을 통한 기능 구현 필요
@Tag(name = "AccountController", description = "계정 관련 기능 api")
@RestController
@RequestMapping(value = "/api/account")
//@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AccountController {
    private final RestApiController restApiController;


}
