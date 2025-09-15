package side.eventful.application.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class EmailVerificationCriteria {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Request {
        private String email;
        private LocalDateTime expiryDateTime;

        public static Request create(String email, LocalDateTime expiryDateTime) {
            return new Request(email, expiryDateTime);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Verify {
        private String email;
        private String verificationCode;
        private LocalDateTime verificationDateTime;

        public static Verify create(String email, String verificationCode, LocalDateTime verificationDateTime) {
            return new Verify(email, verificationCode, verificationDateTime);
        }
    }
}
