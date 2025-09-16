package side.eventful.domain.member;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateEmailNotExists(MemberCommand.ValidateEmailNotExists command) {
        if (command.getEmail() == null || command.getEmail().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        if (memberRepository.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
    }

    public void validateNicknameNotExists(MemberCommand.ValidateNicknameNotExists command) {
        if (command.getNickname() == null || command.getNickname().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }

        if (memberRepository.existsByNickname(command.getNickname())) {
            throw new IllegalArgumentException("이미 등록된 닉네임입니다.");
        }
    }

    public Member create(MemberCommand.Create command) {

        validateEmailNotExists(MemberCommand.ValidateEmailNotExists.create(command.getEmail()));
        validateNicknameNotExists(MemberCommand.ValidateNicknameNotExists.create(command.getNickname()));

        Member member = Member.create(command.getEmail(), command.getPassword(), command.getNickname(), passwordEncoder);

        return memberRepository.save(member);
    }

}
