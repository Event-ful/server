package side.eventful.application.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EmailVerificationResult {

    @Getter
    @AllArgsConstructor
    public static class Request {
        private final String verificationCode;

        public static Request create(String verificationCode) {
            return new Request(verificationCode);
        }
    }
}
