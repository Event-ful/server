package side.eventful.domain.member.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class SessionAuthService implements AuthService{

    private static final String USER_ID_SESSION_KEY = "USER_ID";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;

    @Override
    public void login(AuthCommand.Login command) {
        Member member = memberRepository.findByEmail(command.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(command.getPassword(), member.getPasswordHash())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        httpSession.setAttribute(USER_ID_SESSION_KEY, member.getId());
    }

    @Override
    public void logout() {
        httpSession.invalidate();
    }
}
