package side.eventful.application.eventgroup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.eventful.domain.eventgroup.EventGroupCommand;
import side.eventful.domain.eventgroup.EventGroupService;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.auth.AuthService;

@Service
@RequiredArgsConstructor
public class EventGroupFacade {

    private final EventGroupService eventGroupService;
    private final AuthService authService;

    public EventGroupResult.Create create(EventGroupCriteria.Create criteria) {

        Member member = authService.getAuthenticatedMember();

        EventGroupCommand.Create command = EventGroupCommand.Create.create(criteria.getName(), criteria.getDescription(), criteria.getImageUrl(), member);

        EventGroup createdEventGroup = eventGroupService.create(command);

        return EventGroupResult.Create.create(
            createdEventGroup.getId(),
            createdEventGroup.getName(),
            createdEventGroup.getDescription(),
            createdEventGroup.getImageUrl(),
            createdEventGroup.getJoinPassword()
        );
    }

}
