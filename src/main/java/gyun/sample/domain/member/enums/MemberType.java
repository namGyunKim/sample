package gyun.sample.domain.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import gyun.sample.domain.account.enums.AccountRole;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum MemberType {

    GENERAL("일반"),
    GOOGLE("구글"),
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
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    public boolean checkSocialType() {
        return this == GOOGLE;
    }

    // 소셜 타입의 기본 권한을 반환하는 메서드 추가
    public AccountRole getDefaultRole() {
        return AccountRole.USER;
    }
}