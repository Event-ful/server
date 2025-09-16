package side.eventful.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberCommand {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class ValidateEmailNotExists {
        private String email;

        public static ValidateEmailNotExists create(String email) {
            return new ValidateEmailNotExists(email);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class ValidateNicknameNotExists {
        private String nickname;

        public static ValidateNicknameNotExists create(String nickname) {
            return new ValidateNicknameNotExists(nickname);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private String nickname;
        private String email;
        private String password;

        public static Create create(String nickname, String email, String password) {
            return new Create(nickname, email, password);
        }
    }
}
