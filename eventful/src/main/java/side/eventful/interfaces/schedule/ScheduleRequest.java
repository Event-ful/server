package side.eventful.interfaces.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class ScheduleRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {

        @NotNull(message = "이벤트 ID는 필수입니다")
        @JsonProperty("event_id")
        private Long eventId;

        @NotBlank(message = "일정명은 필수입니다")
        @JsonProperty("name")
        private String name;

        @JsonProperty("memo")
        private String memo;

        @NotBlank(message = "시작 시간은 필수입니다")
        @JsonProperty("start_time")
        private String startTime; // "HH:mm" 형식

        @NotBlank(message = "종료 시간은 필수입니다")
        @JsonProperty("end_time")
        private String endTime; // "HH:mm" 형식

        @NotBlank(message = "장소는 필수입니다")
        @JsonProperty("location")
        private String location;

        // 테스트용 팩토리 메서드
        public static Create of(Long eventId, String name, String memo,
                                String startTime, String endTime, String location) {
            Create request = new Create();
            request.eventId = eventId;
            request.name = name;
            request.memo = memo;
            request.startTime = startTime;
            request.endTime = endTime;
            request.location = location;
            return request;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SetAmount {

        @NotNull(message = "금액은 필수입니다")
        @PositiveOrZero(message = "금액은 0 이상이어야 합니다")
        @JsonProperty("amount")
        private BigDecimal amount;

        // 테스트용 팩토리 메서드
        public static SetAmount of(BigDecimal amount) {
            SetAmount request = new SetAmount();
            request.amount = amount;
            return request;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SetReceiptFile {

        @NotBlank(message = "파일 경로는 필수입니다")
        @JsonProperty("file_path")
        private String filePath;

        // 테스트용 팩토리 메서드
        public static SetReceiptFile of(String filePath) {
            SetReceiptFile request = new SetReceiptFile();
            request.filePath = filePath;
            return request;
        }
    }
}

