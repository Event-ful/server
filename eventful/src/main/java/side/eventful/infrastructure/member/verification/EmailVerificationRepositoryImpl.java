package side.eventful.infrastructure.member.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import side.eventful.domain.member.verification.EmailVerification;
import side.eventful.domain.member.verification.EmailVerificationRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {
    private final EmailVerificationJpaRepository emailVerificationJpaRepository;

    @Override
    public Optional<EmailVerification> findLatestByEmail(String email) {
        return emailVerificationJpaRepository.findTopByEmailOrderByExpiryDateTimeDesc(email);
    }

    @Override
    public EmailVerification save(EmailVerification emailVerification) {
        return emailVerificationJpaRepository.save(emailVerification);
    }

    @Override
    public Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode) {
        return emailVerificationJpaRepository.findByEmailAndVerificationCode(email, verificationCode);
    }

    @Override
    public void delete(EmailVerification emailVerification) {
        emailVerificationJpaRepository.delete(emailVerification);
    }

    @Override
    public boolean existsByVerificationCode(String code) {
        return emailVerificationJpaRepository.existsByVerificationCode(code);
    }
}
