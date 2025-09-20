package side.eventful.domain.member.verification;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import side.eventful.global.error.exception.BusinessException;
import side.eventful.global.error.exception.ErrorDivision;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;

    /**
     * 이메일 인증 생성
     *
     * @param command
     * @return
     */
    public EmailVerification create(EmailVerificationCommand.Create command) {
        if (command.getEmail() == null
            || command.getEmail().isEmpty()) {
            throw new IllegalArgumentException("이메일이 입력되지 않았습니다.");
        }

        emailVerificationRepository.findLatestByEmail(command.getEmail())
            .ifPresent(emailVerificationRepository::delete);

        String verificationCode = verificationCodeGenerator.generate();
        LocalDateTime expiryDateTime = command.getExpiryDateTime();
        if (expiryDateTime == null) {
            expiryDateTime = LocalDateTime.now().plusMinutes(30);
        }

        EmailVerification emailVerification = EmailVerification.create(command.getEmail(), verificationCode, expiryDateTime);

        return emailVerificationRepository.save(emailVerification);
    }

    /**
     * 이메일 인증
     *
     * @param command
     */
    public void verifyEmail(EmailVerificationCommand.VerifyEmail command) {
        EmailVerification emailAndVerificationCode = emailVerificationRepository.findByEmailAndVerificationCode(command.getEmail(), command.getVerificationCode())
            .orElseThrow(
                () -> new BusinessException(HttpStatus.BAD_REQUEST.value(), "인증코드가 유효하지 않습니다.", ErrorDivision.EMAIL_CONFIRM_INVALID_CODE)
            );

        emailAndVerificationCode.verify(command.getVerificationDateTime());

        emailVerificationRepository.save(emailAndVerificationCode);
    }

    /**
     * 인증된 이메일인지 확인
     *
     * @param command
     */
    public void validateVerifiedEmail(EmailVerificationCommand.ValidateVerifiedEmail command) {

        EmailVerification emailAndVerificationCode = emailVerificationRepository.findByEmailAndVerificationCode(command.getEmail(), command.getVerificationCode())
            .orElseThrow(
                () -> new IllegalArgumentException("인증되지 않은 이메일입니다. 이메일 인증을 요청해주세요.")
            );

        if (!emailAndVerificationCode.isVerified()) {
            throw new IllegalArgumentException("인증되지 않은 이메일입니다. 이메일 인증을 진행해주세요.");
        }
    }
}
