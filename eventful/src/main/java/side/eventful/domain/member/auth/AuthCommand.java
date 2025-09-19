package side.eventful.domain.member.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthCommand {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Login {
        private String email;
        private String password;
        public static Login create(String email, String password) {
            return new Login(email, password);
        }
    }
}
