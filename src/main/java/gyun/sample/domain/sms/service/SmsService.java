package gyun.sample.domain.sms.service;

import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.repository.MemberRepository;
import gyun.sample.domain.sms.entity.SMS;
import gyun.sample.domain.sms.payload.request.FindPasswordRequest;
import gyun.sample.domain.sms.payload.request.SMSRequest;
import gyun.sample.domain.sms.repository.SMSRepository;
import gyun.sample.global.enums.GlobalActiveEnums;
import gyun.sample.global.exception.GlobalException;
import gyun.sample.global.exception.enums.ErrorCode;
import gyun.sample.global.utils.UtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmsService {

    @Value("${sms.key}")
    private String apiKey;

    @Value("${sms.secret}")
    private String apiSecret;

    @Value("${sms.from}")
    private String from;

    private final SMSRepository smsRepository;

    private DefaultMessageService messageService;

    private final MemberRepository memberRepository;

    private void initializeMessageService() {
        if (this.messageService == null) {
            this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        }
    }

    public String sendVerificationCode(String countryCode, String phoneNumber) {
        try {

            final String formattedPhoneNumber = UtilService.removeNonDigits(phoneNumber);
            final String formattedCountryCode = UtilService.removeNonDigits(countryCode);
            // 메시지 서비스 초기화
            initializeMessageService();

            // 인증번호 생성
            String verificationCode = generateVerificationCode();

            // 메시지 내용
            String content = "Your verification code is: " + verificationCode;

            // 국제 표준 형식으로 전화번호 변환

            Message message = new Message();
            // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
            log.info("Sending SMS to: {}", formattedPhoneNumber);
            log.info("from: {}", from);
            message.setFrom(from);
            message.setCountry(formattedCountryCode);
            message.setTo(formattedPhoneNumber);
            message.setText(content);

            this.messageService.sendOne(new SingleMessageSendingRequest(message));
            SMS sms = new SMS(formattedPhoneNumber, verificationCode);
            smsRepository.save(sms);
            return "success";
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            log.error("SMS 전송 실패", e);
            return e.getMessage();
        }
    }

    public boolean verify(SMSRequest request) {
        final String formattedPhoneNumber = UtilService.removeNonDigits(request.phoneNumber());
        final String formattedCountryCode = UtilService.removeNonDigits(request.countryCode());
        SMS sms = smsRepository.findByPhoneNumberAndVerificationCodeAndVerified(formattedPhoneNumber, formattedCountryCode, false)
                .orElseThrow(() -> new GlobalException(ErrorCode.VERIFICATION_CODE_INVALID));
        sms.verify();
        return true;
    }

    public String verifyWithLoginId(SMSRequest request) {
        boolean isSuccess = verify(request);
        if (isSuccess) {
            final String formattedPhoneNumber = UtilService.removeNonDigits(request.phoneNumber());
            final String formattedCountryCode = UtilService.removeNonDigits(request.countryCode());
            Member member = memberRepository.findByPhoneNumberAndCountryCodeAndActive((formattedPhoneNumber), formattedCountryCode, GlobalActiveEnums.ACTIVE)
                    .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_EXIST));
            return member.getLoginId();
        } else {
            return "fail";
        }
    }


    private String generateVerificationCode() {
        // 6자리 랜덤 인증번호 생성
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999 범위
        return String.valueOf(code);
    }

    public boolean findPassword(FindPasswordRequest request) {
        final String formattedPhoneNumber = UtilService.removeNonDigits(request.phoneNumber());
        final String formattedCountryCode = UtilService.removeNonDigits(request.countryCode());
        boolean exists = memberRepository.existsByLoginIdAndPhoneNumberAndCountryCodeAndActive((request.loginId()), formattedPhoneNumber, formattedCountryCode, GlobalActiveEnums.ACTIVE);
        if (!exists) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_EXIST);
        }

        sendVerificationCode(formattedCountryCode, formattedPhoneNumber);

        return true;
    }
}