package gyun.sample.domain.s3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface S3Service {

    String upload(MultipartFile file, long entityId) throws IOException;

    void deleteFile(long entityId);

    String getFileUrl(long entityId);
}
