package side.eventful.infrastructure.eventgroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface EventGroupJpaRepository extends JpaRepository<EventGroup, Long> {
    Optional<EventGroup> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);

    @Query("SELECT DISTINCT eg FROM EventGroup eg JOIN eg.members m WHERE m.member = :member")
    List<EventGroup> findByMember(@Param("member") Member member);
}
