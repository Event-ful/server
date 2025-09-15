package side.eventful.domain.member.verification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String verificationCode;

    private LocalDateTime expiryDateTime;

    private boolean isVerified;

    private EmailVerification(String email, String verificationCode, LocalDateTime expiryDateTime) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiryDateTime = expiryDateTime;
        this.isVerified = false;
    }

    public static EmailVerification create(String email, String verificationCode, LocalDateTime expiryDateTime) {
        return new EmailVerification(email, verificationCode, expiryDateTime);
    }

    /**
     * 이메일 인증 완료 처리
     * @param verificationDateTime 인증 시간
     */
    public void verify(LocalDateTime verificationDateTime) {
        if (isVerified) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }

        if (verificationDateTime.isAfter(expiryDateTime)){
            throw new IllegalStateException("인증 시간이 만료되었습니다.");
        }

        isVerified = true;
    }

}
