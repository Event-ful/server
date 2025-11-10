package side.eventful.interfaces.eventgroup;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.eventful.application.eventgroup.EventGroupCriteria;
import side.eventful.application.eventgroup.EventGroupFacade;
import side.eventful.application.eventgroup.EventGroupResult;
import side.eventful.global.response.ApiResponse;

@AllArgsConstructor
@RestController
@RequestMapping("/api/event-group")
public class EventGroupController {

    private final EventGroupFacade eventGroupFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<EventGroupResponse.Create>> createEventGroup(
        @RequestBody @Valid EventGroupRequest.Create request) {

        EventGroupResult.Create result = eventGroupFacade.create(
            EventGroupCriteria.Create.create(request.getName(), request.getDescription(), request.getImageUrl())
        );

        return ResponseEntity.ok(
            ApiResponse.ok(EventGroupResponse.Create.create(
                result.getId(),
                result.getName(),
                result.getDescription(),
                result.getImageUrl(),
                result.getJoinPassword()
            ))
        );
    }

    @PostMapping("/{group-id}/join")
    public ResponseEntity<ApiResponse<EventGroupResponse.Join>> joinEventGroup(
        @PathVariable("group-id") Long groupId,
        @RequestBody @Valid EventGroupRequest.Join request) {

        eventGroupFacade.joinGroup(
            EventGroupCriteria.Join.create(groupId, request.getGroupPassword())
        );

        return ResponseEntity.ok(
            ApiResponse.ok(EventGroupResponse.Join.create(groupId))
        );
    }

    @PutMapping("/{group-id}")
    public ResponseEntity<ApiResponse> updateEventGroup(
        @PathVariable("group-id") Long groupId,
        @RequestBody @Valid EventGroupRequest.Update request) {

        eventGroupFacade.updateGroup(
            EventGroupCriteria.Update.create(groupId, request.getGroupName(), request.getGroupDescription(), request.getGroupImage())
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/{group-id}")
    public ResponseEntity<ApiResponse<EventGroupResponse.Get>> getEventGroup(
        @PathVariable("group-id") Long groupId) {

        EventGroupResult.Get result = eventGroupFacade.getGroup(
            EventGroupCriteria.Get.create(groupId)
        );

        // GroupMember 변환
        java.util.List<EventGroupResponse.GroupMember> responseMembers = result.getGroupMembers()
                .stream()
                .map(member -> EventGroupResponse.GroupMember.create(
                    member.getMemberId(),
                    member.getMemberName(),
                    member.isLeader()
                ))
                .toList();

        return ResponseEntity.ok(
            ApiResponse.ok(EventGroupResponse.Get.create(
                result.getGroupName(),
                result.getGroupDescription(),
                result.isLeader(),
                result.getMemberCount(),
                result.getJoinCode(),
                result.getGroupPassword(),
                responseMembers
            ))
        );
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<EventGroupResponse.VerifyCode>> verifyJoinCode(
        @RequestBody @Valid EventGroupRequest.VerifyCode request) {

        EventGroupResult.VerifyCode result = eventGroupFacade.verifyJoinCode(
            EventGroupCriteria.VerifyCode.create(request.getJoinCode())
        );

        return ResponseEntity.ok(
            ApiResponse.ok(EventGroupResponse.VerifyCode.create(
                result.getGroupId(),
                result.getGroupName(),
                result.getGroupDescription()
            ))
        );
    }

    @DeleteMapping("/{group-id}/members/{member-id}")
    public ResponseEntity<ApiResponse> removeMember(
        @PathVariable("group-id") Long groupId,
        @PathVariable("member-id") Long memberId) {

        eventGroupFacade.removeMember(
            EventGroupCriteria.RemoveMember.create(groupId, memberId)
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<EventGroupResponse.GetList>> getEventGroups() {
        EventGroupResult.GetList result = eventGroupFacade.getGroupList(
            EventGroupCriteria.GetList.create()
        );

        java.util.List<EventGroupResponse.GroupSummary> responseGroups = result.getGroups()
                .stream()
                .map(group -> EventGroupResponse.GroupSummary.create(
                    group.getGroupId(),
                    group.getGroupName(),
                    group.getGroupDescription(),
                    group.getGroupImageUrl(),
                    group.getMemberCount()
                ))
                .toList();

        return ResponseEntity.ok(
            ApiResponse.ok(EventGroupResponse.GetList.create(responseGroups))
        );
    }
}
