package gyun.sample.domain.account.payload.response;

import gyun.sample.global.exception.enums.ErrorCode;

public record TokenResponse(
        Long id,                        //회원 아이디
        String loginId,         //로그인 아이디
        String role,            //권한
        String nickName,             //닉네임
        String memberType,  //회원 타입
        ErrorCode errorCode //에러 코드
) {

    public String getErrorCodeNumber(){
        return errorCode.getCode();
    }


    public static TokenResponse generatedGuest(ErrorCode errorCode) {
        return new TokenResponse(0L, "GUEST", "GUEST", "GUEST", "GENERAL", errorCode);
    }
}
