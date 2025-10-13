package side.eventful.domain.eventgroup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventGroupService {

    private final EventGroupRepository eventGroupRepository;

    public EventGroup create(EventGroupCommand.Create command) {
        EventGroup eventGroup = EventGroup.create(command.getName(), command.getDescription(), command.getImageUrl(), command.getLeader());

        return eventGroupRepository.save(eventGroup);
    }
}
