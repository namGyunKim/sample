package gyun.sample.domain.board.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum BoardType {

    FREE("자유게시판"),
    MATCHING("매칭게시판"),
    NOTICE("공지사항"),
    QNA("Q&A"),
    REVIEW("리뷰게시판"),
    FAQ("FAQ"),
    EVENT("이벤트"),
    TIP("팁게시판"),
    ANONYMOUS("익명게시판");
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
                .filter(v -> v.name().equals(requestValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
