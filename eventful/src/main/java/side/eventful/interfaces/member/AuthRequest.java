package side.eventful.interfaces.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthRequest {

    @Getter
    @AllArgsConstructor
    public static class Login {
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        @NotBlank(message = "패스워드는 필수입니다")
        private String password;
    }
}
