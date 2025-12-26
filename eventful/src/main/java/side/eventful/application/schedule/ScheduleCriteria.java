package side.eventful.application.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import side.eventful.domain.member.Member;

import java.time.LocalTime;

/**
 * Controller → Facade 요청 객체
 * Request DTO에서 변환된 애플리케이션 레이어용 객체
 */
public class ScheduleCriteria {

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Create {
        private Long eventId;
        private String name;
        private String memo;
        private LocalTime startTime;
        private LocalTime endTime;
        private String location;
        private Member creator;

        public static Create of(Long eventId, String name, String memo,
                                LocalTime startTime, LocalTime endTime,
                                String location, Member creator) {
            return new Create(eventId, name, memo, startTime, endTime, location, creator);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class SetAmount {
        private Long scheduleId;
        private java.math.BigDecimal amount;
        private Member requester;

        public static SetAmount of(Long scheduleId, java.math.BigDecimal amount, Member requester) {
            return new SetAmount(scheduleId, amount, requester);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class SetReceiptFile {
        private Long scheduleId;
        private String filePath;
        private Member requester;

        public static SetReceiptFile of(Long scheduleId, String filePath, Member requester) {
            return new SetReceiptFile(scheduleId, filePath, requester);
        }
    }

    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Delete {
        private Long scheduleId;
        private Member requester;

        public static Delete of(Long scheduleId, Member requester) {
            return new Delete(scheduleId, requester);
        }
    }
}

