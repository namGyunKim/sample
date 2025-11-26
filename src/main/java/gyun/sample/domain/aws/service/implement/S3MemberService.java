package gyun.sample.domain.aws.service.implement;

import gyun.sample.domain.aws.enums.ImageType;
import gyun.sample.domain.aws.service.common.AbstractS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@Slf4j
public class S3MemberService extends AbstractS3Service {


    // 부모 클래스(AbstractS3Service)가 S3Client를 필요로 하므로 생성자 주입
    public S3MemberService(S3Client s3Client) {
        super(s3Client);
    }

    @Override
    protected void validateImageType(ImageType imageType) {
        // 회원 전용 유효성 검사 호출
        ImageType.validateMemberUploadType(imageType);
    }

}
