package gyun.sample.domain.s3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface S3Service {

    List<String> upload(List<MultipartFile> files, long entityId) throws IOException;

    void deleteFile(List<String> fileNames);

    String getFileUrl(String fileName);
}
