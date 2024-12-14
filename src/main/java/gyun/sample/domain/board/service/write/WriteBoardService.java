package gyun.sample.domain.board.service.write;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.board.payload.request.BoardCreateRequest;
import gyun.sample.domain.board.payload.request.BoardInactiveRequest;
import gyun.sample.domain.board.payload.request.BoardUpdateRequest;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteBoardService {

    GlobalCreateResponse create(BoardCreateRequest boardCreateRequest, CurrentAccountDTO currentAccountDTO);

    GlobalUpdateResponse update(BoardUpdateRequest boardUpdateRequest);

    GlobalInactiveResponse inactive(BoardInactiveRequest boardInactiveRequest, CurrentAccountDTO currentAccountDTO);
}
