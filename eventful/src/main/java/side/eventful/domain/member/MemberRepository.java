package side.eventful.domain.member;

public interface MemberRepository {
    boolean existsByEmail(String email);
    Member save(Member member);
    boolean existsByNickname(String nickname);
}
