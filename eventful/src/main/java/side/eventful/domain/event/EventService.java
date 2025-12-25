package side.eventful.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.member.Member;

/**
 * Event 도메인 서비스
 * Event 집합체 내부의 비즈니스 로직만 담당
 *
 * <p>다른 도메인(EventGroup, Member)과의 협력은
 * application 레이어의 EventFacade에서 처리</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    /**
     * 이벤트 생성
     *
     * @param eventGroup 이벤트가 속할 그룹 (Facade에서 검증 완료된 상태)
     * @param command 이벤트 생성 명령
     * @return 생성된 이벤트
     */
    @Transactional
    public Event create(EventGroup eventGroup, EventCommand.Create command) {
        Member creator = command.getCreator();

        Event event = Event.create(
            eventGroup,
            command.getName(),
            command.getDescription(),
            command.getMaxParticipants(),
            command.getEventDate(),
            command.getPlaceId(),
            creator
        );

        return eventRepository.save(event);
    }
}

