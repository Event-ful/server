package side.eventful.domain.member.verification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import side.eventful.global.error.exception.BusinessException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationTest {

    @Test
    @DisplayName("이메일 인증 생성 - 정상 케이스")
    void create_WithValidInput_ReturnsNewVerification() {
        String email = "email@test.com";
        String verificationCode = "123456";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(10);

        EmailVerification emailVerification =
                EmailVerification.create(email, verificationCode, expiryDateTime);

        assertThat(emailVerification.getEmail()).isEqualTo(email);
        assertThat(emailVerification.getVerificationCode()).isEqualTo(verificationCode);
        assertThat(emailVerification.getExpiryDateTime()).isEqualTo(expiryDateTime);
    }

    @Test
    @DisplayName("인증 - 정상 케이스")
    void verify_WithValidInput_UpdatesVerificationStatus() {
        String email = "email@test.com";
        String verificationCode = "123456";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime verificationDateTime = LocalDateTime.now();

        EmailVerification emailVerification =
            EmailVerification.create(email, verificationCode, expiryDateTime);

        emailVerification.verify(verificationDateTime);

        assertThat(emailVerification.isVerified()).isTrue();
    }

    @Test
    @DisplayName("인증 - 인증 시간이 만료된 경우 에외가 발생한다")
    void verify_WithExpiredTime_ThrowsException() {
        String email = "email@test.com";
        String verificationCode = "123456";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime verificationDateTime = expiryDateTime.plusMinutes(10);

        EmailVerification emailVerification =
            EmailVerification.create(email, verificationCode, expiryDateTime);

        assertThrows(BusinessException.class, () -> emailVerification.verify(verificationDateTime));
    }

    @Test
    @DisplayName("인증 - 이미 인증된 경우 예외가 발생한다")
    void verify_WithAlreadyVerified_ThrowsException() {
        String email = "email@test.com";
        String verificationCode = "123456";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime verificationDateTime = LocalDateTime.now();

        EmailVerification emailVerification =
            EmailVerification.create(email, verificationCode, expiryDateTime);

        emailVerification.verify(verificationDateTime);

        assertThrows(BusinessException.class, () -> emailVerification.verify(verificationDateTime));
    }
}
