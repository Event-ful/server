package side.eventful.infrastructure.event;

import org.springframework.data.jpa.repository.JpaRepository;
import side.eventful.domain.event.Event;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
}

