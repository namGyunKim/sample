package gyun.sample.domain.member.api;


import gyun.sample.domain.member.payload.request.SaveMemberForCustomerRequest;
import gyun.sample.domain.member.payload.response.SaveMemberResponse;
import gyun.sample.domain.member.service.CustomerService;
import gyun.sample.global.api.RestApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CustomerRestController", description = "고객 api")
@RestController
@RequestMapping(value = "/api/customer")
//@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;
    private final RestApiController restApiController;

    @PostMapping(value = "/save")
    public ResponseEntity<String> save(@Valid @RequestBody SaveMemberForCustomerRequest request,
                                           BindingResult bindingResult){
        SaveMemberResponse response = customerService.saveCustomer(request);
        return restApiController.createSuccessRestResponse(response);
    }


}
