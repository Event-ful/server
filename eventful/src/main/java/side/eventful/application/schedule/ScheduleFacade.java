package side.eventful.application.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.eventful.domain.event.Event;
import side.eventful.domain.event.EventRepository;
import side.eventful.domain.member.Member;
import side.eventful.domain.schedule.Schedule;
import side.eventful.domain.schedule.ScheduleCommand;
import side.eventful.domain.schedule.ScheduleRepository;
import side.eventful.domain.schedule.ScheduleService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Schedule 관련 도메인 간 협력을 조율하는 Facade
 *
 * <p>여러 도메인 서비스(ScheduleService, EventRepository)를 조합하여
 * 하나의 유스케이스를 완성합니다.</p>
 *
 * <p>Controller는 이 Facade를 통해 Schedule 관련 기능을 호출합니다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleFacade {

    private final ScheduleService scheduleService;
    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 일정 생성
     *
     * @param criteria Controller에서 전달받은 요청 객체
     * @return 생성된 일정 결과
     */
    @Transactional
    public ScheduleResult.Create createSchedule(ScheduleCriteria.Create criteria) {
        // 1. Event 조회
        Event event = eventRepository.findById(criteria.getEventId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));

        // 2. Schedule 생성
        ScheduleCommand.CreateSchedule command = new ScheduleCommand.CreateSchedule(
            criteria.getName(),
            criteria.getMemo(),
            criteria.getStartTime(),
            criteria.getEndTime(),
            criteria.getLocation()
        );

        Schedule schedule = scheduleService.createSchedule(event, criteria.getCreator(), command);

        return ScheduleResult.Create.of(
            schedule.getId(),
            schedule.getEvent().getId(),
            schedule.getName(),
            schedule.getMemo(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getLocation(),
            schedule.getCreator().getId()
        );
    }

    /**
     * 일정 금액 입력
     *
     * @param criteria 금액 입력 요청
     */
    @Transactional
    public void setAmount(ScheduleCriteria.SetAmount criteria) {
        Schedule schedule = scheduleRepository.findById(criteria.getScheduleId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        ScheduleCommand.SetAmount command = new ScheduleCommand.SetAmount(criteria.getAmount());
        scheduleService.setAmount(schedule, criteria.getRequester(), command);
    }

    /**
     * 영수증 파일 첨부
     *
     * @param criteria 영수증 첨부 요청
     */
    @Transactional
    public void setReceiptFile(ScheduleCriteria.SetReceiptFile criteria) {
        Schedule schedule = scheduleRepository.findById(criteria.getScheduleId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        ScheduleCommand.SetReceiptFile command = new ScheduleCommand.SetReceiptFile(criteria.getFilePath());
        scheduleService.setReceiptFile(schedule, criteria.getRequester(), command);
    }

    /**
     * 일정 삭제
     *
     * @param criteria 삭제 요청
     */
    @Transactional
    public void deleteSchedule(ScheduleCriteria.Delete criteria) {
        Schedule schedule = scheduleRepository.findById(criteria.getScheduleId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        scheduleService.deleteSchedule(schedule, criteria.getRequester());
    }

    /**
     * 특정 이벤트의 모든 일정 조회
     *
     * @param eventId 이벤트 ID
     * @return 일정 목록
     */
    public List<ScheduleResult.Detail> getSchedulesByEvent(Long eventId) {
        List<Schedule> schedules = scheduleService.getSchedulesByEvent(eventId);

        return schedules.stream()
            .map(schedule -> ScheduleResult.Detail.of(
                schedule.getId(),
                schedule.getEvent().getId(),
                schedule.getName(),
                schedule.getMemo(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLocation(),
                schedule.getAmount(),
                schedule.getReceiptFilePath(),
                schedule.getCreator().getId()
            ))
            .collect(Collectors.toList());
    }

    /**
     * 일정 상세 조회
     *
     * @param scheduleId 일정 ID
     * @return 일정 상세 정보
     */
    public ScheduleResult.Detail getScheduleDetail(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        return ScheduleResult.Detail.of(
            schedule.getId(),
            schedule.getEvent().getId(),
            schedule.getName(),
            schedule.getMemo(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getLocation(),
            schedule.getAmount(),
            schedule.getReceiptFilePath(),
            schedule.getCreator().getId()
        );
    }
}

