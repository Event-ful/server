package side.eventful.domain.eventgroup;

import java.util.Optional;

public interface EventGroupRepository {
    EventGroup save(EventGroup eventGroup);
    Optional<EventGroup> findById(Long id);
    Optional<EventGroup> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);
}
