package side.eventful.interfaces.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ScheduleResponse {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {

        @JsonProperty("schedule_id")
        private Long scheduleId;

        @JsonProperty("event_id")
        private Long eventId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("memo")
        private String memo;

        @JsonProperty("start_time")
        private String startTime; // "HH:mm" 형식

        @JsonProperty("end_time")
        private String endTime; // "HH:mm" 형식

        @JsonProperty("location")
        private String location;

        @JsonProperty("creator_id")
        private Long creatorId;

        public static Create of(Long scheduleId, Long eventId, String name, String memo,
                                String startTime, String endTime, String location, Long creatorId) {
            return new Create(scheduleId, eventId, name, memo, startTime, endTime, location, creatorId);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Detail {

        @JsonProperty("schedule_id")
        private Long scheduleId;

        @JsonProperty("event_id")
        private Long eventId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("memo")
        private String memo;

        @JsonProperty("start_time")
        private String startTime; // "HH:mm" 형식

        @JsonProperty("end_time")
        private String endTime; // "HH:mm" 형식

        @JsonProperty("location")
        private String location;

        // TODO: 스펙 확정 후 추가
        // @JsonProperty("amount")
        // private BigDecimal amount;
        //
        // @JsonProperty("receipt_file_path")
        // private String receiptFilePath;

        @JsonProperty("creator_id")
        private Long creatorId;

        public static Detail of(Long scheduleId, Long eventId, String name, String memo,
                                String startTime, String endTime, String location, Long creatorId) {
            return new Detail(scheduleId, eventId, name, memo, startTime, endTime, location, creatorId);
        }
    }
}

