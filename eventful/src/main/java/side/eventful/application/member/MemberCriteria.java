package side.eventful.application.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberCriteria {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Signup {
        private String nickname;
        private String email;
        private String password;
        private String verificationCode;

        public static Signup create(
            String nickname, String email, String password, String verificationCode) {
            return new Signup(nickname, email, password, verificationCode);
        }
    }
}
