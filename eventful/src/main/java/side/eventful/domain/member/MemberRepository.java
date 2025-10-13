package side.eventful.domain.member;

import java.util.Optional;

public interface MemberRepository {
    boolean existsByEmail(String email);
    Member save(Member member);
    boolean existsByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(long id);
}
