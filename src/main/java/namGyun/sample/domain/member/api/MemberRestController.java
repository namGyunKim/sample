package namGyun.sample.domain.member.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import namGyun.sample.domain.member.payload.request.SaveMemberRequest;
import namGyun.sample.domain.member.payload.response.SaveMemberResponse;
import namGyun.sample.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: 2023/09/02 벨리데이터의 예외 처리 및 BindingResult 작업 필요 기본적인 가입만 구현해놓음
@Tag(name = "MemberRestController", description = "유저 api 컨트롤러")
@RestController(value = "MemberRestController Controller")
//@RequestMapping(value = "/api/user", headers = "X_API_VERSION=1")
@RequestMapping(value = "/api/member")
//@SecurityRequirement(name = "Bearer Authentication")
public class MemberRestController {

    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Object> saveUser(SaveMemberRequest request){
        SaveMemberResponse response = memberService.saveUser(request);
        return ResponseEntity.ok(response);
    }


}
