package side.eventful.application.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import side.eventful.IntegrationTestSupport;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;
import side.eventful.domain.member.MemberService;
import side.eventful.domain.member.verification.EmailVerification;
import side.eventful.domain.member.verification.EmailVerificationRepository;
import side.eventful.domain.member.verification.EmailVerificationService;
import side.eventful.global.error.exception.BusinessException;
import side.eventful.infrastructure.email.TestEmailSender;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class EmailVerificationFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private EmailVerificationFacade emailVerificationFacade;
    @Autowired
    private EmailVerificationService emailVerificationService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TestEmailSender emailSender;
    @Autowired
    private TestPasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Test
    @DisplayName("이메일 인증 요청 - 정상 케이스")
    void request_WithValidInput_ReturnsEmailVerificationResult() throws IOException {
        // given
        String email = "test@abcd.com";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(30);
        EmailVerificationCriteria.Request criteria = EmailVerificationCriteria.Request.create(email, expiryDateTime);

        // when
        EmailVerificationResult.Request request = emailVerificationFacade.request(criteria);

        // then
        assertThat(request.getVerificationCode()).isEqualTo("123456");
    }

    @Test
    @DisplayName("이메일 인증 요청 - 이미 존재하는 이메일인 경우 에러가 발생한다.")
    void request_AlreadyExistsEmail_ThrowsException() throws IOException {
        // given
        String email = "test@abcd.com";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(30);
        EmailVerificationCriteria.Request criteria = EmailVerificationCriteria.Request.create(email, expiryDateTime);

        Member member = Member.create(email, "password", "nickName", passwordEncoder);
        memberRepository.save(member);

        // when&then
        assertThatThrownBy(() -> emailVerificationFacade.request(criteria))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 등록된 이메일입니다.");
    }

    @Test
    @DisplayName("이메일 인증 요청 - 이미 인증이 완료된 이메일도 삭제하고 새로 생성한다")
    void request_AlreadyVerifiedEmail_DeleteAndCreateNew() throws IOException {
        // given
        String email = "test@abcd.com";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(30);
        EmailVerification emailVerification = EmailVerification.create(email, "old-code", LocalDateTime.now().plusMinutes(30));
        emailVerification.verify(LocalDateTime.now());
        emailVerificationRepository.save(emailVerification);
        EmailVerificationCriteria.Request criteria = EmailVerificationCriteria.Request.create(email, expiryDateTime);

        // when
        EmailVerificationResult.Request result = emailVerificationFacade.request(criteria);

        // then
        assertThat(result.getVerificationCode()).isEqualTo("123456");

        // 기존 인증된 이메일이 삭제되고 새로운 인증 코드가 생성되었는지 확인
        EmailVerification newVerification = emailVerificationRepository.findLatestByEmail(email)
            .orElseThrow(() -> new AssertionError("새로운 이메일 인증 정보를 찾을 수 없습니다."));

        assertThat(newVerification.getVerificationCode()).isEqualTo("123456");
        assertThat(newVerification.isVerified()).isFalse(); // 새로 생성된 것은 미인증 상태
    }

    @Test
    @DisplayName("이메일 인증 - 정상 케이스")
    void verify_WithValidInput_Complete() {
        // given
        String email = "test@abcd.com";
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(30);
        String verificationCode = "123456";
        EmailVerification emailVerification = EmailVerification.create(email, verificationCode, LocalDateTime.now().plusMinutes(30));

        emailVerificationRepository.save(emailVerification);

        EmailVerificationCriteria.Verify criteria = EmailVerificationCriteria.Verify.create(email, verificationCode, LocalDateTime.now());
        // when&then
        assertThatCode(() -> emailVerificationFacade.verify(criteria))
            .doesNotThrowAnyException();

        EmailVerification verified = emailVerificationRepository.findByEmailAndVerificationCode(email, verificationCode)
            .orElseThrow(() -> new AssertionError("이메일 인증 정보를 찾을 수 없습니다."));

        assertThat(verified.isVerified()).isTrue();

    }
}
