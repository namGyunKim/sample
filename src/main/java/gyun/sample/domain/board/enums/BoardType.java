package gyun.sample.domain.board.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum BoardType {

    FREE("자유글"), // 일반글
    QUESTION("질문글"), // 질문글
    ALL("전체"); // 전체

    private final String value;

    BoardType(String value) {
        this.value = value;
    }

    public static String toStrings() {
        StringBuilder sb = new StringBuilder();
        for (BoardType option : BoardType.values()) {
            sb.append(option.name()).append("(").append(option.getValue()).append(")").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    //  요청값으로 Enum 매칭
    @JsonCreator
    public static BoardType create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    //    String 으로 name 을 받아서 반환
    public static BoardType getByName(String name) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // 전체를 제외하고 랜덤한 개수의 랜덤한 값을 리스트로 반환
    public static BoardType getRandom() {
        List<BoardType> games = Arrays.stream(values())
                .collect(Collectors.toList());
        Collections.shuffle(games);
        return games.get(0); // 랜덤으로 하나 반환
    }
}
