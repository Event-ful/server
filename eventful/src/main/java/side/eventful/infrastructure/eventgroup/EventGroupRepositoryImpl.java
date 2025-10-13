package side.eventful.infrastructure.eventgroup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.eventgroup.EventGroupRepository;

@Repository
@RequiredArgsConstructor
public class EventGroupRepositoryImpl implements EventGroupRepository {

    private EventGroupJpaRepository eventGroupJpaRepository;

    @Override
    public EventGroup save(EventGroup eventGroup) {
        return eventGroupJpaRepository.save(eventGroup);
    }
}
