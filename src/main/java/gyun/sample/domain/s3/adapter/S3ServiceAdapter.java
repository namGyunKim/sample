package gyun.sample.domain.s3.adapter;

import gyun.sample.domain.aws.service.S3Service;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.domain.s3.service.S3MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3ServiceAdapter {

    private final S3MemberService s3MemberService;


    public S3Service getService(UploadDirect uploadDirect) {
        return switch (uploadDirect) {
            case MEMBER_PROFILE -> s3MemberService;
            // 다른 서비스들도 필요 시 추가
            default -> throw new IllegalArgumentException("지원하지 않는 업로드 엔티티 타입입니다: " + uploadDirect);
        };
    }
}