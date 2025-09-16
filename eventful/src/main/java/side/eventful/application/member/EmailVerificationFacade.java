package side.eventful.application.member;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.eventful.domain.member.MemberCommand;
import side.eventful.domain.member.MemberService;
import side.eventful.domain.member.verification.EmailVerification;
import side.eventful.domain.member.verification.EmailVerificationCommand;
import side.eventful.domain.member.verification.EmailVerificationService;
import side.eventful.infrastructure.email.EmailSender;

import java.io.IOException;

@Transactional
@AllArgsConstructor
@Service
public class EmailVerificationFacade {

    private final EmailVerificationService emailVerificationService;
    private final MemberService memberService;
    private final EmailSender emailSender;

    public EmailVerificationResult.Request request(EmailVerificationCriteria.Request criteria) throws IOException {

        memberService.validateEmailNotExists(MemberCommand.ValidateEmailNotExists.create(criteria.getEmail()));
        EmailVerification emailVerification = emailVerificationService.create(EmailVerificationCommand.Create.create(criteria.getEmail(), criteria.getExpiryDateTime()));

        emailSender.sendVerificationEMail(emailVerification.getEmail(), emailVerification.getVerificationCode());

        return EmailVerificationResult.Request.create(emailVerification.getVerificationCode());
    }

    public void verify(EmailVerificationCriteria.Verify criteria) {
        emailVerificationService.verifyEmail(EmailVerificationCommand.VerifyEmail.create(criteria.getEmail(), criteria.getVerificationCode(), criteria.getVerificationDateTime()));
    }

}
