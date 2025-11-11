package side.eventful.domain.member.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;

import java.util.Collections;

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

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            member.getEmail(), // principal (로그인 ID)
            null, // credentials (비밀번호는 저장하지 않음)
            Collections.emptyList() // authorities (권한 목록, 필요시 추가)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ⭐ 세션에 SecurityContext 저장 (Spring Security가 자동으로 처리하지만 명시적으로 확인)
        httpSession.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        );

        httpSession.setAttribute(USER_ID_SESSION_KEY, member.getId());
    }

    @Override
    public void logout() {
        httpSession.invalidate();
    }

    @Override
    public Member getAuthenticatedMember() {
        Object userId = httpSession.getAttribute(USER_ID_SESSION_KEY);

        if (userId == null) {
            throw new IllegalArgumentException("로그인 된 사용자가 없습니다.");
        }

        return memberRepository.findById((Long)userId)
            .orElseThrow(() -> new IllegalArgumentException("인증된 사용자가 없습니다."));

    }
}
