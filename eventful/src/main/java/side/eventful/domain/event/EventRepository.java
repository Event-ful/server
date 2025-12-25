package side.eventful.domain.event;

import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(Long id);
    void delete(Event event);
}

