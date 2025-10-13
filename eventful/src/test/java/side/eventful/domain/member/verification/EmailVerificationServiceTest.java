package side.eventful.domain.member.verification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import side.eventful.global.error.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {
    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Test
    @DisplayName("이메인 인증 생성 - 정상 케이스")
    void create_WithValidInput_ReturnsNewVerification() {
        // given
        String email = "test@abcd.com";
        LocalDateTime expiryDateTime = LocalDateTime.now();
        String verificationCode = "123456";
        EmailVerificationCommand.Create command = EmailVerificationCommand.Create.create(email, expiryDateTime);

        given(emailVerificationRepository.findLatestByEmail(email))
            .willReturn(Optional.empty());
        given(verificationCodeGenerator.generate())
            .willReturn(verificationCode);

        ArgumentCaptor<EmailVerification> emailVerificationCaptor =
            ArgumentCaptor.forClass(EmailVerification.class);
        given(emailVerificationRepository.save(emailVerificationCaptor.capture()))
            .willAnswer(invocation -> invocation.getArgument(0));

        // when
        EmailVerification result = emailVerificationService.create(command);

        // then
        assertThat(result)
            .satisfies(verification -> {
                assertThat(verification.getEmail()).isEqualTo(email);
                assertThat(verification.getVerificationCode()).isEqualTo(verificationCode);
                assertThat(verification.getExpiryDateTime()).isEqualTo(expiryDateTime);
            });

        verify(emailVerificationRepository).findLatestByEmail(email);
        verify(verificationCodeGenerator).generate();
        verify(emailVerificationRepository).save(any(EmailVerification.class));

        EmailVerification savedVerification = emailVerificationCaptor.getValue();
        assertThat(savedVerification)
            .satisfies(verification -> {
                assertThat(verification.getEmail()).isEqualTo(email);
                assertThat(verification.getVerificationCode()).isEqualTo(verificationCode);
                assertThat(verification.getExpiryDateTime()).isEqualTo(expiryDateTime);
            });
    }

    @Test
    @DisplayName("이메일 인증 생성 - 기존 이메일이 있는 경우 삭제하고 다시 생성한다")
    void create_WithExistingEmail_DeleteAndReturnNewVerificationCode() {
        // given
        String email = "test@abcd.com";
        LocalDateTime expiryDateTime = LocalDateTime.now();
        String verificationCode = "123456";
        EmailVerificationCommand.Create command = EmailVerificationCommand.Create.create(email, expiryDateTime);

        EmailVerification existingVerification = EmailVerification.create(email, "old-code", LocalDateTime.now().minusHours(1));

        given(emailVerificationRepository.findLatestByEmail(email))
            .willReturn(Optional.of(existingVerification));
        given(verificationCodeGenerator.generate())
            .willReturn(verificationCode);
        given(emailVerificationRepository.save(any(EmailVerification.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        // when
        EmailVerification result = emailVerificationService.create(command);

        // then
        assertThat(result.getVerificationCode()).isEqualTo(verificationCode);

        verify(emailVerificationRepository).findLatestByEmail(email);
        verify(emailVerificationRepository).delete(existingVerification);
        verify(verificationCodeGenerator).generate();
        verify(emailVerificationRepository).save(any(EmailVerification.class));
    }

    @Test
    @DisplayName("이메일 인증 - 정상 케이스")
    void verify_WithValidInput_VerifyEmailVerification(){
        // given
        String email = "test@abcd.com";
        String verificationCode = "123456";

        EmailVerificationCommand.VerifyEmail command = EmailVerificationCommand.VerifyEmail.create(email, verificationCode, LocalDateTime.now());

        EmailVerification verification = EmailVerification.create(email, verificationCode, LocalDateTime.now().plusMinutes(30));

        given(emailVerificationRepository.findByEmailAndVerificationCode(email, verificationCode))
            .willReturn(Optional.of(verification));

        // when
        emailVerificationService.verifyEmail(command);

        // then
        assertThat(verification.isVerified()).isTrue();

        verify(emailVerificationRepository).findByEmailAndVerificationCode(email, verificationCode);
        verify(emailVerificationRepository).save(verification);
    }

    @Test
    @DisplayName("이메일 인증 검증 - 정상 케이스")
    void validateVerifiedEmail_WithVerifiedEmail_Completes() {
        // given
        String email = "test@abcd.com";
        String verificationCode = "123456";

        EmailVerificationCommand.ValidateVerifiedEmail command = EmailVerificationCommand.ValidateVerifiedEmail.create(email, verificationCode);

        EmailVerification verification = EmailVerification.create(email, verificationCode, LocalDateTime.now().plusMinutes(30));
        verification.verify(LocalDateTime.now());
        given(emailVerificationRepository.findByEmailAndVerificationCode(email, verificationCode))
            .willReturn(Optional.of(verification));

        // when & then
        assertThatCode(() -> emailVerificationService.validateVerifiedEmail(command))
            .doesNotThrowAnyException();

        verify(emailVerificationRepository).findByEmailAndVerificationCode(email, verificationCode);
    }

    @Test
    @DisplayName("이메일 인증 검증 - 인증되지 않은 이메일인 경우 예외가 발생한다")
    void validateVerifiedEmail_WithUnVerifiedEmail_ThrowsException() {
        // given
        String email = "test@abcd.com";
        String verificationCode = "123456";

        EmailVerificationCommand.ValidateVerifiedEmail command = EmailVerificationCommand.ValidateVerifiedEmail.create(email, verificationCode);

        EmailVerification verification = EmailVerification.create(email, verificationCode, LocalDateTime.now().plusMinutes(30));
        given(emailVerificationRepository.findByEmailAndVerificationCode(email, verificationCode))
            .willReturn(Optional.of(verification));

        // when & then
        assertThatThrownBy(() -> emailVerificationService.validateVerifiedEmail(command))
            .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증되지 않은 이메일입니다. 이메일 인증을 진행해주세요.");

        verify(emailVerificationRepository).findByEmailAndVerificationCode(email, verificationCode);
    }
}
