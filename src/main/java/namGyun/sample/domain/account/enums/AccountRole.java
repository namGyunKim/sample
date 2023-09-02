package namGyun.sample.domain.account.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum AccountRole {
    SUPER_ADMIN("최고 관리자"),
    ADMIN("관리자"),
    CUSTOMER("사용자");
    private final String value;

    AccountRole(String value) {
        this.value = value;
    }
    @JsonCreator
    public static AccountRole create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
