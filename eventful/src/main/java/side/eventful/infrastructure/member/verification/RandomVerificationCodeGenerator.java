package side.eventful.infrastructure.member.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import side.eventful.domain.member.verification.EmailVerificationRepository;
import side.eventful.domain.member.verification.VerificationCodeGenerator;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class RandomVerificationCodeGenerator implements VerificationCodeGenerator {
    private final EmailVerificationRepository emailVerificationRepository;
    private static final SecureRandom secureRandom;
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;

    static {
        secureRandom = new SecureRandom();
        // 초기 시드 생성을 미리 수행
        secureRandom.nextBytes(new byte[64]);
    }

    @Override
    public String generate() {
        int attempts = 0;
        String code;

        do {
            if (attempts >= MAX_ATTEMPTS) {
                throw new IllegalStateException("인증 코드 생성 최대 시도 횟수(" + MAX_ATTEMPTS + ")를 초과했습니다.");
            }

            code = generateSecureCode();
            attempts++;

        } while (isCodeDuplicate(code));

        return code;
    }

    private String generateSecureCode() {
        int number = secureRandom.nextInt(1000000);
        return String.format("%0" + CODE_LENGTH + "d", number);
    }

    private boolean isCodeDuplicate(String code) {
        return emailVerificationRepository.existsByVerificationCode(code);
    }
}
