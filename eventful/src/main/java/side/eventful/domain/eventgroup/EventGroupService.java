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

    public void joinGroup(EventGroupCommand.Join command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        eventGroup.joinMember(command.getMember(), command.getJoinPassword());

        eventGroupRepository.save(eventGroup);
    }

    public void updateGroup(EventGroupCommand.Update command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        eventGroup.updateGroup(command.getName(), command.getDescription(), command.getImageUrl(), command.getRequestMember());

        eventGroupRepository.save(eventGroup);
    }

    public EventGroup getGroup(EventGroupCommand.Get command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        // 그룹 멤버인지 확인
        if (!eventGroup.getMembers().contains(command.getRequestMember())) {
            throw new IllegalArgumentException("그룹에 속하지 않은 사용자입니다");
        }

        return eventGroup;
    }
}
