package side.eventful.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import side.eventful.domain.member.Member;

import java.time.LocalDate;

/**
 * Controller → Facade 요청 객체
 * Request DTO에서 변환된 애플리케이션 레이어용 객체
 */
public class EventCriteria {

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
            return new Create(eventGroupId, name, description, maxParticipants,
                eventDate, placeId, creator);
        }
    }
}

