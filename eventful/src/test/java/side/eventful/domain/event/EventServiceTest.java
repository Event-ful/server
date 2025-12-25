package side.eventful.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.fixture.MemberFixture;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * EventService 단위 테스트
 *
 * <p>Event 도메인 내부 로직만 테스트합니다.
 * 도메인 간 협력(그룹 조회, 권한 검증)은 EventFacadeTest에서 테스트합니다.</p>
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private final PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @BeforeEach
    void setUp() {
        MemberFixture.resetSequence();
    }

    @Nested
    @DisplayName("이벤트 생성")
    class CreateEvent {

        @Test
        @DisplayName("검증된 그룹과 생성자로 이벤트를 생성하면 성공한다")
        void create_withValidGroupAndCreator_success() {
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

            given(eventRepository.save(any(Event.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Event result = eventService.create(eventGroup, command);

            // then
            assertThat(result.getName()).isEqualTo("제주도 여행");
            assertThat(result.getDescription()).isEqualTo("2박 3일 제주도 여행");
            assertThat(result.getMaxParticipants()).isEqualTo(10);
            assertThat(result.getEventDate()).isEqualTo(LocalDate.of(2025, 3, 15));
            assertThat(result.getCreator()).isEqualTo(creator);
            assertThat(result.getEventGroup()).isEqualTo(eventGroup);
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("생성된 이벤트에 생성자가 자동으로 참여자로 등록된다")
        void create_creatorAutoParticipates() {
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

            given(eventRepository.save(any(Event.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Event result = eventService.create(eventGroup, command);

            // then
            assertThat(result.getParticipants()).hasSize(1);
            assertThat(result.getParticipants().get(0).getMember()).isEqualTo(creator);
            assertThat(result.getParticipants().get(0).getRole()).isEqualTo(ParticipantRole.CREATOR);
        }

        @Test
        @DisplayName("최대 참여 인원 없이 이벤트를 생성할 수 있다 (무제한)")
        void create_withoutMaxParticipants_success() {
            // given
            Member creator = MemberFixture.createWithId("creator@test.com", "password", "creator", passwordEncoder);
            EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", null, creator);

            EventCommand.Create command = EventCommand.Create.of(
                1L,
                "제주도 여행",
                "2박 3일 제주도 여행",
                null,  // 무제한
                LocalDate.of(2025, 3, 15),
                null,
                creator
            );

            given(eventRepository.save(any(Event.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Event result = eventService.create(eventGroup, command);

            // then
            assertThat(result.getMaxParticipants()).isNull();
            assertThat(result.isFull()).isFalse();
        }

        @Test
        @DisplayName("장소 없이 이벤트를 생성할 수 있다")
        void create_withoutPlace_success() {
            // given
            Member creator = MemberFixture.createWithId("creator@test.com", "password", "creator", passwordEncoder);
            EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", null, creator);

            EventCommand.Create command = EventCommand.Create.of(
                1L,
                "제주도 여행",
                "2박 3일 제주도 여행",
                10,
                LocalDate.of(2025, 3, 15),
                null,  // 장소 없음
                creator
            );

            given(eventRepository.save(any(Event.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Event result = eventService.create(eventGroup, command);

            // then
            assertThat(result.getPlaceId()).isNull();
        }
    }
}

