package side.eventful.infrastructure.member.verification;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import side.eventful.domain.member.verification.VerificationCodeGenerator;

@Component
@Primary
@Profile("test")
public class TestVerificationCodeGenerator implements VerificationCodeGenerator {
    @Override
    public String generate() {
        return "123456";
    }
}
