package gyun.sample.global.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum GlobalOrderEnums {
    CREATE_ASC("생성일 오름차순"),
    CREATE_DESC("생성일 내림차순");
    private final String value;

    GlobalOrderEnums(String value) {
        this.value = value;
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public GlobalOrderEnums create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    public static boolean checkAdminMember(GlobalOrderEnums order) {
        List<GlobalOrderEnums> allowedValues = Arrays.asList(CREATE_ASC, CREATE_DESC);
        return allowedValues.contains(order);
    }
}
