package side.eventful.application.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Facade → Controller 응답 객체
 * Domain Entity를 변환한 애플리케이션 레이어용 결과 객체
 */
public class ScheduleResult {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private Long scheduleId;
        private Long eventId;
        private String name;
        private String memo;
        private LocalTime startTime;
        private LocalTime endTime;
        private String location;
        private Long creatorId;

        public static Create of(Long scheduleId, Long eventId, String name, String memo,
                                LocalTime startTime, LocalTime endTime, String location,
                                Long creatorId) {
            return new Create(scheduleId, eventId, name, memo, startTime, endTime, location, creatorId);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Detail {
        private Long scheduleId;
        private Long eventId;
        private String name;
        private String memo;
        private LocalTime startTime;
        private LocalTime endTime;
        private String location;
        private BigDecimal amount;
        private String receiptFilePath;
        private Long creatorId;

        public static Detail of(Long scheduleId, Long eventId, String name, String memo,
                                LocalTime startTime, LocalTime endTime, String location,
                                BigDecimal amount, String receiptFilePath, Long creatorId) {
            return new Detail(scheduleId, eventId, name, memo, startTime, endTime,
                location, amount, receiptFilePath, creatorId);
        }
    }
}

