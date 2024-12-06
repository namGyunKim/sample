package gyun.sample.domain.sms.repository;

import gyun.sample.domain.sms.entity.SMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SMSRepository extends JpaRepository<SMS, Long> {

    Optional<SMS> findByPhoneNumberAndVerificationCodeAndVerified(String phoneNumber, String verificationCode, boolean verified);

}
