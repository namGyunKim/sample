package gyun.sample.domain.log.service.read;

import gyun.sample.domain.log.entity.MemberLog;
import gyun.sample.domain.log.payload.request.MemberLogRequest;
import gyun.sample.domain.log.payload.response.MemberLogResponse;
import gyun.sample.domain.log.repository.MemberLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadMemberLogService {

    private final MemberLogRepository memberLogRepository;

    /**
     * 회원 활동 로그 목록 조회
     * - 생성일 기준 내림차순 정렬
     * - 검색어(loginId)가 있으면 필터링
     */
    public Page<MemberLogResponse> getMemberLogs(MemberLogRequest request) {
        // 페이지는 0부터 시작하므로 page - 1 처리
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<MemberLog> logs;

        // 검색어가 존재하는 경우 필터링 조회
        if (request.getSearchWord() != null && !request.getSearchWord().isBlank()) {
            logs = memberLogRepository.findByLoginIdContainingIgnoreCase(request.getSearchWord(), pageable);
        } else {
            // 검색어가 없으면 전체 조회
            logs = memberLogRepository.findAll(pageable);
        }

        return logs.map(MemberLogResponse::new);
    }
}