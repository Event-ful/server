package side.eventful.infrastructure.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.eventful.infrastructure.email.senderGrid.SenderGridUtil;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {
    private final SenderGridUtil senderGridUtil;

    @Override
    public void sendVerificationEMail(String toEmail, String verificationCode) throws IOException {
        String subject = "회원가입 인증 코드";
        String contents = "인증 코드: " + verificationCode;

        senderGridUtil.sendEmail(subject, toEmail, contents);
    }
}
