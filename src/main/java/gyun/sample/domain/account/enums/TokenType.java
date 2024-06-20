package gyun.sample.domain.account.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum TokenType {
    ACCESS("엑세스 토큰"), REFRESH("리프레시 토큰");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public static TokenType create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
