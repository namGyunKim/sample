package gyun.sample.domain.account.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum AccountRole {
    SUPER_ADMIN("최고 관리자"),
    ADMIN("관리자"),
    CUSTOMER("사용자"),
    GUEST("손님");
    private final String value;

    AccountRole(String value) {
        this.value = value;
    }


//    toStrings
    public static List<String> toStrings() {
        List<String> list = new ArrayList<>();
        for (AccountRole option : AccountRole.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(option.name()).append("(").append(option.getValue()).append(")");
            list.add(sb.toString());
        }
        return list;
    }
    //  name 값으로 Enum 매칭
    @JsonCreator
    public static AccountRole createByName(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

//    value 값으로 Enum 매칭
    public static AccountRole createByValue(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.getValue().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
