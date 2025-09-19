package side.eventful.infrastructure.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberJpaRepository.existsByNickname(nickname);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email);
    }
}
