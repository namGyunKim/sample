package gyun.sample.domain.s3.api;

import gyun.sample.domain.s3.S3Service;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor

public class S3Controller {

    private final S3Service s3Service;
    private final RestApiController restApiController;

    @Operation(summary = "파일 업로드", description = "지정된 디렉토리에 파일을 업로드합니다.")
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam UploadDirect directory) {

        try {
            String etag = s3Service.uploadFileWithDisposition(directory, file);
            return restApiController.createSuccessRestResponse(etag);
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Operation(summary = "파일 삭제", description = "지정된 파일을 삭제합니다.")
    @PostMapping(value = "/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName,
                                             @RequestParam UploadDirect uploadDirect) {
        s3Service.deleteFile(fileName, uploadDirect);
        return restApiController.createSuccessRestResponse(fileName);
    }

    @Operation(summary = "파일 url을가져옴", description = "지정된 파일의 url을 가져옵니다.")
    @PostMapping(value = "/url")
    public ResponseEntity<String> getFileUrl(@RequestParam String fileName,
                                             @RequestParam UploadDirect uploadDirect) {
        return restApiController.createSuccessRestResponse(s3Service.getFileUrl(fileName, uploadDirect));
    }
}
