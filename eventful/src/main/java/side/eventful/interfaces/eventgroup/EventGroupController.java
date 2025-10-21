package side.eventful.interfaces.eventgroup;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse> joinEventGroup(
        @PathVariable Long groupId,
        @RequestBody @Valid EventGroupRequest.Join request) {

        eventGroupFacade.joinGroup(
            EventGroupCriteria.Join.create(groupId, request.getJoinPassword())
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse> updateEventGroup(
        @PathVariable Long groupId,
        @RequestBody @Valid EventGroupRequest.Update request) {

        eventGroupFacade.updateGroup(
            EventGroupCriteria.Update.create(groupId, request.getGroupName(), request.getGroupDescription(), request.getGroupImage())
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
