package gyun.sample.domain.member.repository;

import gyun.sample.domain.member.entity.MemberImage;
import gyun.sample.domain.s3.enums.UploadDirect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {

    Optional<MemberImage> findByFileNameAndUploadDirect(String fileName, UploadDirect uploadDirect);

}
