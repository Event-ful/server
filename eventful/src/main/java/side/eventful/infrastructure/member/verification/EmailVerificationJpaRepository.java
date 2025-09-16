package side.eventful.infrastructure.member.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import side.eventful.domain.member.verification.EmailVerification;

import java.util.Optional;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerification, Long>{
    Optional<EmailVerification> findTopByEmailOrderByExpiryDateTimeDesc(String email);
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
    boolean existsByVerificationCode(String code);
}
