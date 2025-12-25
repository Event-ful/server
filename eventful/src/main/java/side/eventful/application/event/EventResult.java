package side.eventful.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Facade → Controller 응답 객체
 * Domain Entity를 변환한 애플리케이션 레이어용 결과 객체
 */
public class EventResult {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private Long eventId;
        private Long eventGroupId;
        private String name;
        private String description;
        private Integer maxParticipants;
        private LocalDate eventDate;
        private String placeId;

        public static Create of(Long eventId, Long eventGroupId, String name,
                                 String description, Integer maxParticipants,
                                 LocalDate eventDate, String placeId) {
            return new Create(eventId, eventGroupId, name, description,
                maxParticipants, eventDate, placeId);
        }
    }
}

