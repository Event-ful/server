package side.eventful.domain.schedule;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 일정(Schedule) 관련 Command 객체
 */
public class ScheduleCommand {

    /**
     * 일정 생성 커맨드
     */
    public record CreateSchedule(
        String name,
        String memo,
        LocalTime startTime,
        LocalTime endTime,
        String location
    ) {
        public CreateSchedule {
            // Record Compact Constructor - validation은 도메인에서 수행
        }
    }

    /**
     * 일정 금액 입력 커맨드
     */
    public record SetAmount(
        BigDecimal amount
    ) {
    }

    /**
     * 영수증 파일 경로 설정 커맨드
     */
    public record SetReceiptFile(
        String filePath
    ) {
    }
}

