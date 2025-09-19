package side.eventful.domain.member.auth;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionAuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private SessionAuthService sessionAuthService;

    @Test
    @DisplayName("로그인 - 정상 케이스")
    void login_WithValidInput_Complete() {
        // given
        String email = "test@test.com";
        String password = "password";
        AuthCommand.Login command = AuthCommand.Login.create(email, password);

        Member member = Member.create(email, password, "nickname", passwordEncoder);
        given(memberRepository.findByEmail(email))
            .willReturn(Optional.of(member));

        given(passwordEncoder.matches(password, member.getPasswordHash()))
            .willReturn(true);

        // when
        sessionAuthService.login(command);

        // then
        verify(httpSession).setAttribute("USER_ID", member.getId());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_WithNonExistentEmail_ThrowsException() {
        // given
        String email = "nonexistent@test.com";
        String password = "password";
        AuthCommand.Login command = AuthCommand.Login.create(email, password);

        given(memberRepository.findByEmail(email))
            .willReturn(Optional.empty());

        // when, then
        assertThrows(IllegalArgumentException.class, () -> sessionAuthService.login(command));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_WithInvalidPassword_ThrowsException() {
        // given
        String email = "test@test.com";
        String password = "wrongPassword";
        AuthCommand.Login command = AuthCommand.Login.create(email, password);

        Member member = Member.create(email, "correctPassword", "nickname", passwordEncoder);
        given(memberRepository.findByEmail(email))
            .willReturn(Optional.of(member));

        given(passwordEncoder.matches(password, member.getPasswordHash()))
            .willReturn(false);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> sessionAuthService.login(command));
    }

    @Test
    @DisplayName("로그아웃 - 세션 무효화")
    void logout_InvalidatesSession() {
        // when
        sessionAuthService.logout();

        // then
        verify(httpSession).invalidate();
    }

}
