package side.eventful.interfaces.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberRequest {

    @Getter
    @AllArgsConstructor
    public static class CheckNickname {
        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class RequestEmailVerification {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

    }

    @Getter
    @AllArgsConstructor
    public static class ConfirmEmailVerification {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "인증코드는 필수입니다.")
        private String verificationCode;
    }

    @Getter
    @AllArgsConstructor
    public static class Signup {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;

        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;

        @NotBlank(message = "인증코드는 필수입니다.")
        private String verificationCode;
    }



}
