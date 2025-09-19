package side.eventful.domain.member.auth;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.IntegrationTestSupport;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionAuthServiceIntegraionTest extends IntegrationTestSupport {

    @Autowired
    private SessionAuthService sessionAuthService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession httpSession;

    @Test
    @DisplayName("로그인 - 로그아웃 정상 케이스")
    void loginAndLogout() {
        // given
        String email = "test@test.com";
        String password = "password123";
        String nickname = "tester";

        Member member = Member.create(email, password, nickname, passwordEncoder);
        memberRepository.save(member);

        AuthCommand.Login loginCommand = AuthCommand.Login.create(email, password);

        // when
        sessionAuthService.login(loginCommand);

        // then
        assertThat(httpSession.getAttribute("USER_ID")).isNotNull();
        assertThat(httpSession.getAttribute("USER_ID")).isEqualTo(member.getId());

        // when
        sessionAuthService.logout();

        // then
        assertThat(httpSession.getAttribute("USER_ID")).isNull();
    }
}
