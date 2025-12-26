package side.eventful.interfaces.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.eventful.application.schedule.ScheduleCriteria;
import side.eventful.application.schedule.ScheduleFacade;
import side.eventful.application.schedule.ScheduleResult;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.auth.AuthService;
import side.eventful.global.response.ApiResponse;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Schedule(일정) API Controller
 *
 * <p>일정 관련 HTTP 요청을 처리합니다.</p>
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleFacade scheduleFacade;
    private final AuthService authService;

    /**
     * 일정 생성
     *
     * POST /api/schedules
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse.Create>> createSchedule(
        @RequestBody @Valid ScheduleRequest.Create request) {

        Member creator = authService.getAuthenticatedMember();

        ScheduleResult.Create result = scheduleFacade.createSchedule(
            ScheduleCriteria.Create.of(
                request.getEventId(),
                request.getName(),
                request.getMemo(),
                parseTime(request.getStartTime()),
                parseTime(request.getEndTime()),
                request.getLocation(),
                creator
            )
        );

        return ResponseEntity.ok(
            ApiResponse.ok(ScheduleResponse.Create.of(
                result.getScheduleId(),
                result.getEventId(),
                result.getName(),
                result.getMemo(),
                result.getStartTime().toString(),
                result.getEndTime().toString(),
                result.getLocation(),
                result.getCreatorId()
            ))
        );
    }

    /**
     * 특정 이벤트의 모든 일정 조회
     *
     * GET /api/schedules?event_id={eventId}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponse.Detail>>> getSchedulesByEvent(
        @RequestParam("event_id") Long eventId) {

        List<ScheduleResult.Detail> results = scheduleFacade.getSchedulesByEvent(eventId);

        List<ScheduleResponse.Detail> responses = results.stream()
            .map(result -> ScheduleResponse.Detail.of(
                result.getScheduleId(),
                result.getEventId(),
                result.getName(),
                result.getMemo(),
                result.getStartTime().toString(),
                result.getEndTime().toString(),
                result.getLocation(),
                result.getCreatorId()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    /**
     * 일정 상세 조회
     *
     * GET /api/schedules/{scheduleId}
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleResponse.Detail>> getScheduleDetail(
        @PathVariable Long scheduleId) {

        ScheduleResult.Detail result = scheduleFacade.getScheduleDetail(scheduleId);

        return ResponseEntity.ok(
            ApiResponse.ok(ScheduleResponse.Detail.of(
                result.getScheduleId(),
                result.getEventId(),
                result.getName(),
                result.getMemo(),
                result.getStartTime().toString(),
                result.getEndTime().toString(),
                result.getLocation(),
                result.getCreatorId()
            ))
        );
    }

    // TODO: 금액 입력 API - 스펙 확정 후 추가
    // PUT /api/schedules/{scheduleId}/amount

    // TODO: 영수증 파일 첨부 API - 스펙 확정 후 추가
    // PUT /api/schedules/{scheduleId}/receipt

    /**
     * 일정 삭제
     *
     * DELETE /api/schedules/{scheduleId}
     */
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse> deleteSchedule(
        @PathVariable Long scheduleId) {

        Member requester = authService.getAuthenticatedMember();

        scheduleFacade.deleteSchedule(
            ScheduleCriteria.Delete.of(scheduleId, requester)
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 시간 문자열을 LocalTime으로 파싱
     *
     * @param timeString "HH:mm" 형식
     * @return LocalTime
     */
    private LocalTime parseTime(String timeString) {
        return LocalTime.parse(timeString);
    }
}

