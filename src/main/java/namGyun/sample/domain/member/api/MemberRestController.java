package namGyun.sample.domain.member.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import namGyun.sample.domain.member.payload.request.SaveMemberRequest;
import namGyun.sample.domain.member.payload.response.SaveMemberResponse;
import namGyun.sample.domain.member.service.MemberService;
import namGyun.sample.global.api.RestApiController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: 2023/09/02 예외처리 및 로그이벤트 기능 추가 실질적인 예외처리 프로세스 작업 필요
@Tag(name = "MemberRestController", description = "유저 api 컨트롤러")
@RestController(value = "MemberRestController Controller")
//@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
@RequestMapping(value = "/api/member")
@RequiredArgsConstructor
//@SecurityRequirement(name = "Bearer Authentication")
public class MemberRestController{

    private final MemberService memberService;
    private final RestApiController restApiController;

    @PostMapping(value = "/save")
    public ResponseEntity<String> saveUser(@Valid @RequestBody SaveMemberRequest request,
                                           BindingResult bindingResult){
        SaveMemberResponse response = memberService.saveUser(request);
        return restApiController.createSuccessRestResponse(response);
    }


}
