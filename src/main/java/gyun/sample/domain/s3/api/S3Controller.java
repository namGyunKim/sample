package gyun.sample.domain.s3.api;

import gyun.sample.domain.account.payload.dto.CurrentAccountDTO;
import gyun.sample.domain.s3.adapter.S3ServiceAdapter;
import gyun.sample.domain.s3.enums.UploadDirect;
import gyun.sample.domain.s3.service.S3Service;
import gyun.sample.global.annotaion.CurrentAccount;
import gyun.sample.global.api.RestApiController;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor

public class S3Controller {

    private final RestApiController restApiController;
    private final S3ServiceAdapter s3ServiceAdapter;


    @Operation(summary = "파일 업로드", description = "지정된 디렉토리에 파일을 업로드합니다.")
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> uploadFile(
            @RequestParam List<MultipartFile> files,
            @RequestParam UploadDirect uploadDirect,
            @CurrentAccount CurrentAccountDTO currentAccountDTO) {

        try {
            S3Service s3Service = s3ServiceAdapter.getService(uploadDirect);
            List<String> eTags = s3Service.upload(files, currentAccountDTO.id());
            return restApiController.createSuccessRestResponse(eTags);
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Operation(summary = "파일 삭제", description = "지정된 파일을 삭제합니다.")
    @PostMapping(value = "/delete")
    public ResponseEntity<String> deleteFile(@RequestParam List<String> fileNames,
                                             @RequestParam UploadDirect uploadDirect) {
        S3Service s3Service = s3ServiceAdapter.getService(uploadDirect);
        s3Service.deleteFile(fileNames);
        return restApiController.createSuccessRestResponse(fileNames);
    }

    @Operation(summary = "파일 url을가져옴", description = "지정된 파일의 url을 가져옵니다.")
    @PostMapping(value = "/url")
    public ResponseEntity<String> getFileUrl(@RequestParam String fileName,
                                             @RequestParam UploadDirect uploadDirect) {
        S3Service s3Service = s3ServiceAdapter.getService(uploadDirect);
        return restApiController.createSuccessRestResponse(s3Service.getFileUrl(fileName));
    }
}
