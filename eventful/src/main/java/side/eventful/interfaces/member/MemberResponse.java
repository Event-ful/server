package side.eventful.interfaces.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberResponse {

    @Getter
    @AllArgsConstructor
    public static class RequestEmailVerification {
        private String verificationCode;

        public static RequestEmailVerification create(String verificationCode) {
            return new RequestEmailVerification(verificationCode);
        }
    }
}
