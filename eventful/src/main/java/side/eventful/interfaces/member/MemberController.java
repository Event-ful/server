package side.eventful.interfaces.member;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.eventful.application.member.*;
import side.eventful.domain.member.MemberCommand;
import side.eventful.domain.member.MemberService;
import side.eventful.global.response.ApiResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberFacade memberFacade;
    private final EmailVerificationFacade emailVerificationFacade;
    private final MemberService memberService;

    @PostMapping("/signup/check-nickname")
    public ResponseEntity<ApiResponse> checkNickname(@RequestBody @Valid MemberRequest.CheckNickname request) {
        memberService.validateNicknameNotExists(MemberCommand.ValidateNicknameNotExists.create(request.getNickname()));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/signup/verify-email")
    public ResponseEntity<ApiResponse<MemberResponse.RequestEmailVerification>> requestEmailVerification(
        @RequestBody @Valid MemberRequest.RequestEmailVerification request) throws IOException {
        EmailVerificationResult.Request requestEmailVerification = emailVerificationFacade.request(EmailVerificationCriteria.Request.create(request.getEmail(), LocalDateTime.now().plusMinutes(30)));

        return ResponseEntity.ok(
            ApiResponse.ok(MemberResponse.RequestEmailVerification.create(requestEmailVerification.getVerificationCode()))
        );
    }

    @PostMapping("/signup/verify-email/confirm")
    public ResponseEntity<ApiResponse> confirmEmailVerification(@RequestBody @Valid MemberRequest.ConfirmEmailVerification request) {
        emailVerificationFacade.verify(EmailVerificationCriteria.Verify.create(request.getEmail(), request.getVerificationCode(), LocalDateTime.now()));

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody @Valid MemberRequest.Signup request) {
        memberFacade.signup(
            MemberCriteria.Signup.create(request.getNickname(), request.getEmail(), request.getPassword(), request.getVerificationCode())
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

}
