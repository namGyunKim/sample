package gyun.sample.domain.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
//입장 메시지, 퇴장 메시지, 채팅 메시지
public enum MemberType {

    GENERAL("일반"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글"),
    FACEBOOK("페이스북"),
    APPLE("애플");


    private final String value;

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