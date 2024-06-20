package gyun.sample.global.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum GlobalActiveEnums {
    ALL("전체"),
    ACTIVE("활성"),
    INACTIVE("비활성"),
    ;
    private final String value;

    GlobalActiveEnums(String value) {
        this.value = value;
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public GlobalActiveEnums create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
