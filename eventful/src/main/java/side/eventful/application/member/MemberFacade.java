package side.eventful.application.member;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import side.eventful.domain.member.verification.EmailVerificationCommand;
import side.eventful.domain.member.verification.EmailVerificationService;
import side.eventful.domain.member.MemberCommand;
import side.eventful.domain.member.MemberService;

@Service
@AllArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final EmailVerificationService emailVerificationService;

    public void signup(MemberCriteria.Signup criteria) {
        emailVerificationService.validateVerifiedEmail(EmailVerificationCommand.ValidateVerifiedEmail.create(criteria.getEmail(), criteria.getVerificationCode()));

        memberService.create(MemberCommand.Create.create(criteria.getNickname(), criteria.getEmail(), criteria.getPassword()));
    }

}
