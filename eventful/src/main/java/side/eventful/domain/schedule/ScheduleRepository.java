package side.eventful.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * 특정 이벤트의 모든 일정 조회
     */
    @Query("SELECT s FROM Schedule s WHERE s.event.id = :eventId")
    List<Schedule> findByEventId(@Param("eventId") Long eventId);

    /**
     * 특정 이벤트에서 시간이 겹치는 일정 조회
     * (시간 중복 검증을 위해 사용)
     */
    @Query("""
        SELECT s FROM Schedule s 
        WHERE s.event.id = :eventId 
        AND (
            (s.startTime < :endTime AND s.endTime > :startTime)
        )
    """)
    List<Schedule> findOverlappingSchedules(
        @Param("eventId") Long eventId,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
}

