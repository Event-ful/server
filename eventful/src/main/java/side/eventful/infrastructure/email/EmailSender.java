package side.eventful.infrastructure.email;

import java.io.IOException;

public interface EmailSender {
    public void sendVerificationEMail(String to, String verificationCode) throws IOException;
}
