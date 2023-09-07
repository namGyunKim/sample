package gyun.sample.domain.member.api;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.response.InformationCustomerForAdminResponse;
import gyun.sample.domain.member.service.AdminService;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminRestController", description = "관리자 api")
@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminRestController {

    private final AdminService adminService;

    protected final RestApiController restApiController;

    @GetMapping(value = "/information-customer-for-admin/{loginId}")
    public ResponseEntity<String> informationForAdmin(@CurrentAccount CurrentAccountDTO account,
                                                      @PathVariable String loginId){
        InformationCustomerForAdminResponse response = adminService.informationCustomerForAdmin(account,loginId);
        return restApiController.createSuccessRestResponse(response);
    }

}
