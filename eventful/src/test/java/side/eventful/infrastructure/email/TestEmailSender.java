package side.eventful.infrastructure.email;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Primary
@Profile("test")
public class TestEmailSender implements EmailSender {

    @Override
    public void sendVerificationEMail(String email, String verificationCode) throws IOException {
        System.out.println(String.format("Email sent to %s with verification code %s", email, verificationCode));
    }

}
