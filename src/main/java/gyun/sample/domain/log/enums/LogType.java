package gyun.sample.domain.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogType {
    JOIN("회원가입"),
    LOGIN("로그인"),
    LOGIN_FAIL("로그인 실패"), // [추가] 로그인 실패 타입
    UPDATE("정보수정"),
    INACTIVE("탈퇴/비활성화"),
    PASSWORD_CHANGE("비밀번호 변경");

    private final String description;
}