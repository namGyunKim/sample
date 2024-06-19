package gyun.sample.domain.member.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum MemberOrderEnums {
    CREATE_ASC("생성일 오름차순"),
    CREATE_DESC("생성일 내림차순");
    private final String value;

    MemberOrderEnums(String value) {
        this.value = value;
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public static MemberOrderEnums create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    public static List<MemberOrderEnums> getAdminMember() {
        return Arrays.asList(CREATE_ASC, CREATE_DESC);
    }
}
