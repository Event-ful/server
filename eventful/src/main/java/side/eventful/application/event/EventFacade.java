package side.eventful.application.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.eventful.domain.event.Event;
import side.eventful.domain.event.EventCommand;
import side.eventful.domain.event.EventService;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.eventgroup.EventGroupService;

/**
 * Event 관련 도메인 간 협력을 조율하는 Facade
 *
 * <p>여러 도메인 서비스(EventService, EventGroupService)를 조합하여
 * 하나의 유스케이스를 완성합니다.</p>
 *
 * <p>Controller는 이 Facade를 통해 Event 관련 기능을 호출합니다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventFacade {

    private final EventService eventService;
    private final EventGroupService eventGroupService;

    /**
     * 이벤트 생성 (Controller에서 호출)
     *
     * @param criteria Controller에서 전달받은 요청 객체
     * @return 생성된 이벤트 결과
     */
    @Transactional
    public EventResult.Create create(EventCriteria.Create criteria) {
        EventCommand.Create command = EventCommand.Create.of(
            criteria.getEventGroupId(),
            criteria.getName(),
            criteria.getDescription(),
            criteria.getMaxParticipants(),
            criteria.getEventDate(),
            criteria.getPlaceId(),
            criteria.getCreator()
        );

        Event event = createEvent(command);

        return EventResult.Create.of(
            event.getId(),
            event.getEventGroup().getId(),
            event.getName(),
            event.getDescription(),
            event.getMaxParticipants(),
            event.getEventDate(),
            event.getPlaceId()
        );
    }

    /**
     * 이벤트 생성 (내부 또는 테스트용)
     *
     * <p>1. EventGroupService를 통해 그룹 조회 및 권한 검증
     * <p>2. EventService를 통해 이벤트 생성
     *
     * @param command 이벤트 생성 명령
     * @return 생성된 이벤트
     * @throws IllegalArgumentException 그룹이 없거나 그룹원이 아닌 경우
     */
    @Transactional
    public Event createEvent(EventCommand.Create command) {
        // 1. EventGroup 도메인: 그룹 조회 및 권한 검증
        EventGroup eventGroup = eventGroupService.getGroupForEventCreation(
            command.getEventGroupId(),
            command.getCreator()
        );

        // 2. Event 도메인: 이벤트 생성
        return eventService.create(eventGroup, command);
    }
}

