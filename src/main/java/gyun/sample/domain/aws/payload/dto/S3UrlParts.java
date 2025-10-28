package gyun.sample.domain.aws.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3UrlParts {

    private String bucketName;
    private String objectKey;
}
