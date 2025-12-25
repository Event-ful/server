package side.eventful.interfaces.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

public class EventResponse {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {

        @JsonProperty("event_id")
        private Long eventId;

        @JsonProperty("event_group_id")
        private Long eventGroupId;

        @JsonProperty("event_name")
        private String eventName;

        @JsonProperty("event_description")
        private String eventDescription;

        @JsonProperty("event_max_member")
        private Integer eventMaxMember;

        @JsonProperty("event_date")
        private LocalDate eventDate;

        @JsonProperty("place_id")
        private String placeId;

        public static Create of(Long eventId, Long eventGroupId, String eventName,
                                 String eventDescription, Integer eventMaxMember,
                                 LocalDate eventDate, String placeId) {
            return new Create(eventId, eventGroupId, eventName, eventDescription,
                eventMaxMember, eventDate, placeId);
        }
    }
}

