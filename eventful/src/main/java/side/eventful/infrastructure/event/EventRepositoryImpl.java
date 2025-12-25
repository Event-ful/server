package side.eventful.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import side.eventful.domain.event.Event;
import side.eventful.domain.event.EventRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;

    @Override
    public Event save(Event event) {
        return eventJpaRepository.save(event);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventJpaRepository.findById(id);
    }

    @Override
    public void delete(Event event) {
        eventJpaRepository.delete(event);
    }
}

