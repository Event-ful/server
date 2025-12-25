package side.eventful.interfaces.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.eventful.application.event.EventCriteria;
import side.eventful.application.event.EventFacade;
import side.eventful.application.event.EventResult;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.auth.AuthService;
import side.eventful.global.response.ApiResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventFacade eventFacade;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse.Create>> createEvent(
        @RequestBody @Valid EventRequest.Create request) {

        Member creator = authService.getAuthenticatedMember();

        EventResult.Create result = eventFacade.create(
            EventCriteria.Create.of(
                request.getEventGroupId(),
                request.getEventName(),
                request.getEventDescription(),
                request.getEventMaxMember(),
                parseEventDate(request.getEventDate()),
                request.getPlaceId(),
                creator
            )
        );

        return ResponseEntity.ok(
            ApiResponse.ok(EventResponse.Create.of(
                result.getEventId(),
                result.getEventGroupId(),
                result.getName(),
                result.getDescription(),
                result.getMaxParticipants(),
                result.getEventDate(),
                result.getPlaceId()
            ))
        );
    }

    private LocalDate parseEventDate(String eventDate) {
        return LocalDate.parse(eventDate);
    }
}

