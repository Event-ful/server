package side.eventful.infrastructure.member;

import org.springframework.data.jpa.repository.JpaRepository;
import side.eventful.domain.member.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
