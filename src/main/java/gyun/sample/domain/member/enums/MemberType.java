package gyun.sample.domain.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum MemberType {

    GENERAL("일반"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글"),
    FACEBOOK("페이스북"),
    APPLE("애플"),
    GUEST("손님"),
    ALL("전체");


    private final String value;

    public static String toStrings() {
        StringBuilder sb = new StringBuilder();
        for (MemberType option : MemberType.values()) {
            sb.append(option.name()).append("(").append(option.getValue()).append(")").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    MemberType(String value) {
        this.value = value;
    }
    //  요청값으로 Enum 매칭
    @JsonCreator
    public static MemberType create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
