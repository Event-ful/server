package side.eventful.domain.member.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class EmailVerificationCommand {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private String email;
        private LocalDateTime expiryDateTime;

        public static Create create(String email, LocalDateTime expiryDateTime) {
            return new Create(email, expiryDateTime);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class VerifyEmail {
        private String email;
        private String verificationCode;
        private LocalDateTime verificationDateTime;

        public static VerifyEmail create(String email, String verificationCode, LocalDateTime verificationDateTime) {
            return new VerifyEmail(email, verificationCode, verificationDateTime);
        }
    }


    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class ValidateVerifiedEmail {
        private String email;
        private String verificationCode;


        public static ValidateVerifiedEmail create(String email, String verificationCode) {
            return new ValidateVerifiedEmail(email, verificationCode);
        }
    }
}
