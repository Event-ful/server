package side.eventful.infrastructure.eventgroup;

import org.springframework.data.jpa.repository.JpaRepository;
import side.eventful.domain.eventgroup.EventGroup;

import java.util.Optional;

public interface EventGroupJpaRepository extends JpaRepository<EventGroup, Long> {
    Optional<EventGroup> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);
}
