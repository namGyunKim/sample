package gyun.sample.global.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum GlobalFilterEnums {
    ALL("전체"),
    NICK_NAME("닉네임"),
    LOGIN_ID("로그인 아이디");
    private final String value;

    GlobalFilterEnums(String value) {
        this.value = value;
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public GlobalFilterEnums create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    public static boolean checkAdminMember(GlobalFilterEnums order) {
        List<GlobalFilterEnums> allowedValues = Arrays.asList(ALL, NICK_NAME, LOGIN_ID);
        return allowedValues.contains(order);
    }
}
