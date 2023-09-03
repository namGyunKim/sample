package gyun.sample.domain.member.api;


import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.response.SaveMemberResponse;
import gyun.sample.domain.member.service.CustomerService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: 2023/09/02 예외처리 및 로그이벤트 기능 추가 실질적인 예외처리 프로세스 작업 필요
@Tag(name = "CustomerRestController", description = "고객 api 컨트롤러")
@RestController(value = "CustomerRestController Controller")
//@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
@RequestMapping(value = "/api/member/customer")
//@SecurityRequirement(name = "Bearer Authentication")
public class CustomerRestController extends MemberRestController {

    private final CustomerService customerService;

    public CustomerRestController(RestApiController restApiController, CustomerService customerService) {
        super(restApiController);
        this.customerService = customerService;
    }

    @PostMapping(value = "/save")
    public ResponseEntity<String> save(@Valid @RequestBody SaveMemberForCustomerRequest request,
                                           BindingResult bindingResult){
        SaveMemberResponse response = customerService.saveCustomer(request);
        return restApiController.createSuccessRestResponse(response);
    }


}
