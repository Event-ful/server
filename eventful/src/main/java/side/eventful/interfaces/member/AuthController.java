package side.eventful.interfaces.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.eventful.domain.member.auth.AuthCommand;
import side.eventful.domain.member.auth.SessionAuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SessionAuthService sessionAuthService;

    @PostMapping("/login")
    public ResponseEntity<String> login(AuthRequest.Login request) {
        sessionAuthService.login(AuthCommand.Login.create(request.getEmail(), request.getPassword()));
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        sessionAuthService.logout();
        return ResponseEntity.ok("OK");
    }
}
