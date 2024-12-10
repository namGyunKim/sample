package gyun.sample.domain.board.repository.custom.util;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import gyun.sample.domain.board.entity.QBoard;
import gyun.sample.domain.board.enums.BoardType;
import gyun.sample.domain.board.payload.request.BoardRequestList;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardRepositoryCustomUtil {

    QBoard board = QBoard.board;

    public OrderSpecifier<?> getBoardListOrder(GlobalOrderEnums order) {
        return switch (order) {
            case CREATE_ASC -> new OrderSpecifier<>(Order.ASC, board.createdAt);
            case CREATE_DESC -> new OrderSpecifier<>(Order.DESC, board.createdAt);
        };
    }

    public BooleanBuilder getBoardListFilter(BoardRequestList request) {

        BooleanBuilder builder = new BooleanBuilder();
        GlobalActiveEnums active = request.active();
        if (active != GlobalActiveEnums.ALL) {
            builder.and(board.active.eq(active));
        }
        final String searchWord = request.searchWord();
        if (searchWord != null && !searchWord.isBlank()) {
            addMemberListSearchConditions(builder, request.filter(), searchWord, request.boardType());
        }

        return builder;
    }

    private void addMemberListSearchConditions(BooleanBuilder builder, GlobalFilterEnums filter, String searchWord, BoardType boardType) {

        if (boardType != BoardType.ALL) {
            builder.and(board.boardType.eq(boardType));
        }

        switch (filter) {
            case TITLE -> builder.and(board.title.containsIgnoreCase(searchWord));
            case CONTENT -> builder.and(board.content.containsIgnoreCase(searchWord));
            case ALL -> {
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(board.title.containsIgnoreCase(searchWord));
                orBuilder.or(board.content.containsIgnoreCase(searchWord));
                builder.and(orBuilder);
            }
        }
    }
}