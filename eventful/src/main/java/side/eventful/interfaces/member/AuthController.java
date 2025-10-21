package side.eventful.interfaces.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.eventful.domain.member.auth.AuthCommand;
import side.eventful.domain.member.auth.SessionAuthService;
import side.eventful.global.response.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SessionAuthService sessionAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid AuthRequest.Login request) {
        sessionAuthService.login(AuthCommand.Login.create(request.getEmail(), request.getPassword()));
        return ResponseEntity.ok(ApiResponse.ok("Success"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        sessionAuthService.logout();
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
