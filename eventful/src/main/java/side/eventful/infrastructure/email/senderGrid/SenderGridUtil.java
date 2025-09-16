package side.eventful.infrastructure.email.senderGrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SenderGridUtil {
    private final SendGrid sendGrid;

    @Value("${spring.sendgrid.from}")
    private String fromEmail;

    public void sendEmail(String subject, String toEmail, String contents) throws IOException {

        // 보내는 사람 (발신자)
        Email from = new Email(fromEmail);

        Email to = new Email(toEmail);

        // 내용
        Content content = new Content("text/plain", contents);

        // 발신자, 제목, 수신자, 내용을 합쳐 Mail 객체 생성
        Mail mail = new Mail(from, subject, to, content);

        send(mail);
    }

    private void send(Mail mail) throws IOException {
        sendGrid.addRequestHeader("X-Mock", "true");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        if (400 <= response.getStatusCode()) {
            throw new IOException("Failed to send email: " + response.getBody());
        }
    }
}
