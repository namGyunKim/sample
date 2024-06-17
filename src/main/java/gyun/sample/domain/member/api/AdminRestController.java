package gyun.sample.domain.member.api;


import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminRestController", description = "관리자 api")
@RestController
@RequestMapping(value = "/api/admin",headers = "X-API-VERSION=1")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminRestController {

    //    utils
    protected final RestApiController restApiController;

    @ModelAttribute
    public void addCustomHeader(HttpServletResponse response) {
        response.addHeader("X-Header-1", "YourHeaderValue");
    }

}
