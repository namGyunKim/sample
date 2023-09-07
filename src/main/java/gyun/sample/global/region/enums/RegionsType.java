package gyun.sample.global.region.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

// 지역 타입
@Getter
public enum RegionsType {
    KOREA("ko"),
    USA("en");
    private final String value;

    RegionsType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RegionsType create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
