package side.eventful.interfaces.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthRequest {

    @Getter
    @AllArgsConstructor
    public static class Login {
        private String email;
        private String password;
    }
}
