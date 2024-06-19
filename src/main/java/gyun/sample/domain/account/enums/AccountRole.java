package gyun.sample.domain.account.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum AccountRole {
    SUPER_ADMIN("최고 관리자"),
    ADMIN("관리자"),
    USER("사용자"),
    GUEST("손님");
    private final String value;

    AccountRole(String value) {
        this.value = value;
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public static AccountRole create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    //    String 으로 name 을 받아서 반환
    public static AccountRole getByName(String name) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
