package gyun.sample.domain.searchfilter.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ActiveType {
    ALL("전체"),
    ACTIVE_TYPE("활성화"),
    INACTIVE_TYPE("비활성화");
    private final String value;

    ActiveType(String value) {
        this.value = value;
    }

    public static String toStrings() {
        StringBuilder sb = new StringBuilder();
        for (ActiveType option : ActiveType.values()) {
            sb.append(option.name()).append("(").append(option.getValue()).append(")").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public static ActiveType create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
