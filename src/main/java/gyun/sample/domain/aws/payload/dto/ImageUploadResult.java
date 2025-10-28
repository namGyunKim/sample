package gyun.sample.domain.aws.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResult {

    private String fileName;
    private String width;
    private String height;

}
