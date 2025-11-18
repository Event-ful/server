package side.eventful.domain.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private TestPasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @Test
    void create() {
        String nickname = "nickname";
        String email = "email@abcd.com";
        String password = "secret";

        Member member = Member.create(email, password, nickname, passwordEncoder);

        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPasswordHash()).isEqualTo(passwordEncoder.encode(password));
    }
}
