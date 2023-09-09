package gyun.sample.domain.member.api;


import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.response.InformationCustomerForAdminResponse;
import gyun.sample.domain.member.payload.response.SaveMemberResponse;
import gyun.sample.domain.member.service.CustomerService;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


// TODO: 2023/09/09 CustomerRestController 부터 작업 필요 
@Tag(name = "CustomerRestController", description = "고객 api")
@RestController
@RequestMapping(value = "/api/customer")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class CustomerRestController {

    //    service
    private final CustomerService customerService;
    //    utils
    private final RestApiController restApiController;

    //    고객 정보 저장
    @PostMapping(value = "/save")
    public ResponseEntity<String> save(@Valid @RequestBody SaveMemberForCustomerRequest request,
                                           BindingResult bindingResult){
        SaveMemberResponse response = customerService.saveCustomer(request);
        return restApiController.createSuccessRestResponse(response);
    }

    //    관리자 전용 고객 조회
    @GetMapping(value = "/information-customer-for-admin/{loginId}")
    public ResponseEntity<String> informationForAdmin(@CurrentAccount CurrentAccountDTO account,
                                                      @PathVariable String loginId) {
        InformationCustomerForAdminResponse response = customerService.informationCustomerForAdmin(account, loginId);
        return restApiController.createSuccessRestResponse(response);
    }

    //    고객이 자신의 정보 조회
//    @GetMapping(value = "/information-customer-for-self")
//    public ResponseEntity<String> informationForSelf(@CurrentAccount CurrentAccountDTO account,
//                                                      @PathVariable String loginId) {
//        InformationCustomerForAdminResponse response = customerService.informationCustomerForAdmin(account, loginId);
//        return restApiController.createSuccessRestResponse(response);
//    }


}
