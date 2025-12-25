package side.eventful.application.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.event.Event;
import side.eventful.domain.event.EventCommand;
import side.eventful.domain.event.EventService;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.eventgroup.EventGroupService;
import side.eventful.domain.fixture.MemberFixture;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * EventFacade 단위 테스트
 *
 * <p>도메인 간 협력(EventService, EventGroupService)을 테스트합니다.
 * 각 도메인 서비스의 내부 로직은 개별 서비스 테스트에서 검증합니다.</p>
 */
@ExtendWith(MockitoExtension.class)
class EventFacadeTest {

    @Mock
    private EventService eventService;

    @Mock
    private EventGroupService eventGroupService;

    @InjectMocks
    private EventFacade eventFacade;

    private final PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @BeforeEach
    void setUp() {
        MemberFixture.resetSequence();
    }

    @Nested
    @DisplayName("이벤트 생성")
    class CreateEvent {

        @Test
        @DisplayName("그룹원이 이벤트를 생성하면 성공한다")
        void createEvent_withGroupMember_success() {
            // given
            Member creator = MemberFixture.createWithId("creator@test.com", "password", "creator", passwordEncoder);
            EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", null, creator);

            EventCommand.Create command = EventCommand.Create.of(
                1L,
                "제주도 여행",
                "2박 3일 제주도 여행",
                10,
                LocalDate.of(2025, 3, 15),
                null,
                creator
            );

            Event expectedEvent = Event.create(
                eventGroup,
                command.getName(),
                command.getDescription(),
                command.getMaxParticipants(),
                command.getEventDate(),
                command.getPlaceId(),
                creator
            );

            given(eventGroupService.getGroupForEventCreation(1L, creator))
                .willReturn(eventGroup);
            given(eventService.create(eventGroup, command))
                .willReturn(expectedEvent);

            // when
            Event result = eventFacade.createEvent(command);

            // then
            assertThat(result.getName()).isEqualTo("제주도 여행");
            assertThat(result.getEventGroup()).isEqualTo(eventGroup);
            assertThat(result.getCreator()).isEqualTo(creator);

            verify(eventGroupService).getGroupForEventCreation(1L, creator);
            verify(eventService).create(eventGroup, command);
        }

        @Test
        @DisplayName("존재하지 않는 그룹에 이벤트를 생성하면 예외가 발생한다")
        void createEvent_withNonExistentGroup_throwsException() {
            // given
            Member creator = MemberFixture.createWithId("creator@test.com", "password", "creator", passwordEncoder);

            EventCommand.Create command = EventCommand.Create.of(
                999L,
                "제주도 여행",
                "2박 3일 제주도 여행",
                10,
                LocalDate.of(2025, 3, 15),
                null,
                creator
            );

            given(eventGroupService.getGroupForEventCreation(999L, creator))
                .willThrow(new IllegalArgumentException("존재하지 않는 그룹입니다."));

            // when & then
            assertThatThrownBy(() -> eventFacade.createEvent(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 그룹입니다.");

            verify(eventService, never()).create(any(), any());
        }

        @Test
        @DisplayName("그룹원이 아닌 사용자가 이벤트를 생성하면 예외가 발생한다")
        void createEvent_withNonGroupMember_throwsException() {
            // given
            Member nonMember = MemberFixture.createWithId("outsider@test.com", "password", "outsider", passwordEncoder);

            EventCommand.Create command = EventCommand.Create.of(
                1L,
                "제주도 여행",
                "2박 3일 제주도 여행",
                10,
                LocalDate.of(2025, 3, 15),
                null,
                nonMember
            );

            given(eventGroupService.getGroupForEventCreation(1L, nonMember))
                .willThrow(new IllegalArgumentException("그룹원만 이벤트를 생성할 수 있습니다."));

            // when & then
            assertThatThrownBy(() -> eventFacade.createEvent(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹원만 이벤트를 생성할 수 있습니다.");

            verify(eventService, never()).create(any(), any());
        }
    }
}

