package side.eventful.domain.member.verification;

import java.util.Optional;

public interface EmailVerificationRepository {
    Optional<EmailVerification> findLatestByEmail(String email);
    EmailVerification save(EmailVerification emailVerification);
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
    void delete(EmailVerification emailVerification);
    boolean existsByVerificationCode(String code);
}
