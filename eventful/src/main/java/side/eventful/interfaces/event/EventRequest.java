package side.eventful.interfaces.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EventRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {

        @NotNull(message = "그룹 ID는 필수입니다")
        @JsonProperty("event_group_id")
        private Long eventGroupId;

        @NotBlank(message = "이벤트 이름은 필수입니다")
        @JsonProperty("event_name")
        private String eventName;

        @NotBlank(message = "이벤트 설명은 필수입니다")
        @JsonProperty("event_description")
        private String eventDescription;

        @Positive(message = "최대 인원은 1명 이상이어야 합니다")
        @JsonProperty("event_max_member")
        private Integer eventMaxMember;

        @NotBlank(message = "이벤트 날짜는 필수입니다")
        @JsonProperty("event_date")
        private String eventDate;

        @JsonProperty("place_id")
        private String placeId;

        // 테스트용 팩토리 메서드
        public static Create of(Long eventGroupId, String eventName, String eventDescription,
                                 Integer eventMaxMember, String eventDate, String placeId) {
            Create request = new Create();
            request.eventGroupId = eventGroupId;
            request.eventName = eventName;
            request.eventDescription = eventDescription;
            request.eventMaxMember = eventMaxMember;
            request.eventDate = eventDate;
            request.placeId = placeId;
            return request;
        }
    }
}

