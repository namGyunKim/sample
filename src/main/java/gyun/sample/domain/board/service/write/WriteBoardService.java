package gyun.sample.domain.board.service.write;

import gyun.sample.domain.account.dto.CurrentAccountDTO;
import gyun.sample.domain.board.payload.request.CreateBoardRequest;
import gyun.sample.domain.board.payload.request.InactiveBoardRequest;
import gyun.sample.domain.board.payload.request.UpdateBoardRequest;
import gyun.sample.global.payload.response.GlobalCreateResponse;
import gyun.sample.global.payload.response.GlobalInactiveResponse;
import gyun.sample.global.payload.response.GlobalUpdateResponse;
import org.springframework.stereotype.Service;

@Service
public interface WriteBoardService {

    GlobalCreateResponse create(CreateBoardRequest createBoardRequest, CurrentAccountDTO currentAccountDTO);

    GlobalUpdateResponse update(UpdateBoardRequest updateBoardRequest);

    GlobalInactiveResponse inactive(InactiveBoardRequest inactiveBoardRequest, CurrentAccountDTO currentAccountDTO);
}
