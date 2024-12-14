package gyun.sample.domain.board.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.board.entity.Board;
import gyun.sample.domain.board.entity.QBoard;
import gyun.sample.domain.board.payload.request.BoardListRequest;
import gyun.sample.domain.board.repository.custom.BoardRepositoryCustom;
import gyun.sample.domain.board.repository.custom.util.BoardRepositoryCustomUtil;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final BoardRepositoryCustomUtil boardRepositoryCustomUtil;
    QBoard board = QBoard.board;

    @Override
    public Page<Board> getBoardList(BoardListRequest request, Pageable pageable) {

//        조건 추가
        BooleanBuilder builder = boardRepositoryCustomUtil.getBoardListFilter(request);

        // 동적 정렬 추가
        OrderSpecifier<?> orderSpecifier = boardRepositoryCustomUtil.getBoardListOrder(request.order());

        List<Board> content = jpaQueryFactory.selectFrom(board)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(board.id.count())
                .from(board)
                .where(builder)
                .fetchOne();

        if (total == null) {
            throw new GlobalException(ErrorCode.COUNT_FETCH_ERROR, "게시판 수 조회에 실패하였습니다.");
        }

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }
}
