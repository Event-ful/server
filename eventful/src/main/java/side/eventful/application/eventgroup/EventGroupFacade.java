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

    public void joinGroup(EventGroupCriteria.Join criteria) {
        Member member = authService.getAuthenticatedMember();

        EventGroupCommand.Join command = EventGroupCommand.Join.create(
            criteria.getEventGroupId(),
            member,
            criteria.getGroupPassword()
        );

        eventGroupService.joinGroup(command);
    }

    public void updateGroup(EventGroupCriteria.Update criteria) {
        Member member = authService.getAuthenticatedMember();

        EventGroupCommand.Update command = EventGroupCommand.Update.create(
            criteria.getEventGroupId(),
            criteria.getName(),
            criteria.getDescription(),
            criteria.getImageUrl(),
            member
        );

        eventGroupService.updateGroup(command);
    }

    public EventGroupResult.Get getGroup(EventGroupCriteria.Get criteria) {
        Member member = authService.getAuthenticatedMember();

        EventGroupCommand.Get command = EventGroupCommand.Get.create(
            criteria.getEventGroupId(),
            member
        );

        EventGroup eventGroup = eventGroupService.getGroup(command);

        // 멤버 목록을 정렬하여 변환
        java.util.List<EventGroupResult.GroupMember> groupMembers = eventGroup.getMembersOrderedByLeaderAndName()
                .stream()
                .map(m -> EventGroupResult.GroupMember.create(
                    m.getId(),
                    m.getNickname(),
                    eventGroup.isLeader(m)
                ))
                .toList();

        return EventGroupResult.Get.create(
            eventGroup.getName(),
            eventGroup.getDescription(),
            eventGroup.isLeader(member),
            eventGroup.getMemberCount(),
            eventGroup.getJoinCode(), // 실제 joinCode 필드 사용
            eventGroup.getJoinPassword(),
            groupMembers
        );
    }

    public EventGroupResult.VerifyCode verifyJoinCode(EventGroupCriteria.VerifyCode criteria) {
        EventGroupCommand.VerifyCode command = EventGroupCommand.VerifyCode.create(
            criteria.getJoinCode()
        );

        EventGroup eventGroup = eventGroupService.verifyJoinCode(command);

        return EventGroupResult.VerifyCode.create(
            eventGroup.getId(),
            eventGroup.getName(),
            eventGroup.getDescription()
        );
    }
}
