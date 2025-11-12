package side.eventful.domain.eventgroup;

import side.eventful.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface EventGroupRepository {
    EventGroup save(EventGroup eventGroup);
    Optional<EventGroup> findById(Long id);
    Optional<EventGroup> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);
    List<EventGroup> findByMember(Member member);
    void delete(EventGroup eventGroup);
}
