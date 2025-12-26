package side.eventful.domain.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    /**
     * 특정 이벤트의 모든 투표 조회
     */
    @Query("SELECT v FROM Vote v WHERE v.event.id = :eventId")
    List<Vote> findByEventId(@Param("eventId") Long eventId);

    /**
     * 특정 이벤트에서 시간이 겹치는 투표 조회
     */
    @Query("""
        SELECT v FROM Vote v 
        WHERE v.event.id = :eventId 
        AND (
            (v.startTime < :endTime AND v.endTime > :startTime)
        )
    """)
    List<Vote> findOverlappingVotes(
        @Param("eventId") Long eventId,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    /**
     * 진행 중인 투표만 조회
     */
    @Query("SELECT v FROM Vote v WHERE v.event.id = :eventId AND v.status = 'IN_PROGRESS'")
    List<Vote> findInProgressVotesByEventId(@Param("eventId") Long eventId);
}

