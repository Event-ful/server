package side.eventful.infrastructure.security.validator;

public class PasswordValidator {
    public static void validate(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("비밀번호는 숫자를 포함해야 합니다.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("비밀번호는 소문자를 포함해야 합니다.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("비밀번호는 대문자를 포함해야 합니다.");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("비밀번호는 특수문자를 포함해야 합니다.");
        }
    }

}
