package side.eventful.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import side.eventful.domain.member.Member;

import java.time.LocalDate;

public class EventCommand {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private Long eventGroupId;
        private String name;
        private String description;
        private Integer maxParticipants;
        private LocalDate eventDate;
        private String placeId;
        private Member creator;

        public static Create of(Long eventGroupId, String name, String description,
                                 Integer maxParticipants, LocalDate eventDate,
                                 String placeId, Member creator) {
            return new Create(eventGroupId, name, description, maxParticipants, eventDate, placeId, creator);
        }
    }
}

