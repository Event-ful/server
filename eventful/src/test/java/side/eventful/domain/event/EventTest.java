package side.eventful.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.fixture.MemberFixture;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTest {

    private final PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @BeforeEach
    void setUp() {
        MemberFixture.resetSequence();
    }

    @Test
    @DisplayName("이벤트를 생성할 수 있다")
    void create_validInput_success() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = "이벤트";
        String description = "이벤트 설명";
        Integer maxParticipants = 10;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when
        Event event = Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator);

        // then
        assertThat(event.getName()).isEqualTo(eventName);
        assertThat(event.getDescription()).isEqualTo(description);
        assertThat(event.getMaxParticipants()).isEqualTo(maxParticipants);
        assertThat(event.getEventDate()).isEqualTo(eventDate);
        assertThat(event.getCreator()).isEqualTo(creator);
    }

    @Test
    @DisplayName("이벤트 생성 시 생성자가 자동으로 참여자에 추가된다")
    void create_autoParticipates() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = "이벤트";
        String description = "이벤트 설명";
        Integer maxParticipants = 10;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when
        Event event = Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator);

        // then
        assertThat(event.getParticipants()).hasSize(1);
        assertThat(event.getParticipants().get(0).getMember()).isEqualTo(creator);
        assertThat(event.getParticipants().get(0).getRole()).isEqualTo(ParticipantRole.CREATOR);
    }

    @Test
    @DisplayName("이벤트 생성 시 이벤트명은 필수이다")
    void create_withoutName_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = null;
        String description = "이벤트 설명";
        Integer maxParticipants = 10;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when & then
        assertThatThrownBy(() -> Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이벤트 이름은 필수입니다.");
    }

    @Test
    @DisplayName("이벤트 생성 시 빈 이벤트명은 허용되지 않는다")
    void create_withBlankName_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = "   ";
        String description = "이벤트 설명";
        Integer maxParticipants = 10;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when & then
        assertThatThrownBy(() -> Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이벤트 이름은 필수입니다.");
    }

    @Test
    @DisplayName("이벤트 생성 시 설명은 필수이다")
    void create_withoutDescription_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = "이벤트";
        String description = null;
        Integer maxParticipants = 10;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when & then
        assertThatThrownBy(() -> Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이벤트 설명은 필수입니다.");
    }

    @Test
    @DisplayName("이벤트 생성 시 최대 참여자 수는 1명 이상이어야 한다")
    void create_withZeroMaxParticipants_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = "이벤트";
        String description = "이벤트 설명";
        Integer maxParticipants = 0;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when & then
        assertThatThrownBy(() -> Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 참여자 수는 최소 1명 이상입니다.");
    }

    @Test
    @DisplayName("이벤트 생성 시 최대 참여자 수는 null일 수 있다 (무제한)")
    void create_withNullMaxParticipants_success() {
        // given
        Member creator = MemberFixture.createWithId("test@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("test2@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        String eventName = "이벤트";
        String description = "이벤트 설명";
        Integer maxParticipants = null;
        LocalDate eventDate = LocalDate.of(2025, 1, 1);

        // when
        Event event = Event.create(eventGroup, eventName, description, maxParticipants, eventDate, null, creator);

        // then
        assertThat(event.getMaxParticipants()).isNull();
        assertThat(event.isFull()).isFalse();
    }

    @Test
    @DisplayName("일반 회원이 이벤트에 참여할 수 있다")
    void addParticipant_validMember_success() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member participant = MemberFixture.createWithId("participant@example.com", "password", "nickname2", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname3", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);

        // when
        event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // then
        assertThat(event.getParticipants()).hasSize(2);
        assertThat(event.getParticipants())
            .extracting(EventParticipant::getMember)
            .contains(creator, participant);
    }

    @Test
    @DisplayName("동일한 회원은 같은 이벤트에 중복 참여할 수 없다")
    void addParticipant_duplicateMember_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member participant = MemberFixture.createWithId("participant@example.com", "password", "nickname2", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname3", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);
        event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 참여 중인 회원입니다.");
    }

    @Test
    @DisplayName("생성자가 이미 참여자이므로 중복 참여할 수 없다")
    void addParticipant_creator_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);

        // when & then
        assertThatThrownBy(() -> event.addParticipant(creator, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 참여 중인 회원입니다.");
    }

    @Test
    @DisplayName("최대 참여 인원이 찬 이벤트에는 참여할 수 없다")
    void addParticipant_fullEvent_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member participant1 = MemberFixture.createWithId("participant1@example.com", "password", "nickname2", passwordEncoder);
        Member participant2 = MemberFixture.createWithId("participant2@example.com", "password", "nickname3", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname4", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 2, LocalDate.of(2025, 1, 1), null, creator);
        event.addParticipant(participant1, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> event.addParticipant(participant2, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 참여 인원을 초과했습니다.");
    }

    @Test
    @DisplayName("최대 참여 인원이 null이면 무제한으로 참여할 수 있다")
    void addParticipant_unlimitedEvent_success() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", null, LocalDate.of(2025, 1, 1), null, creator);

        // when
        for (int i = 0; i < 100; i++) {
            Member participant = MemberFixture.createWithId("participant" + i + "@example.com", "password", "nickname" + i, passwordEncoder);
            event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());
        }

        // then
        assertThat(event.getParticipants()).hasSize(101); // creator + 100 participants
        assertThat(event.isFull()).isFalse();
    }

    @Test
    @DisplayName("참여자가 이벤트 참여를 취소할 수 있다")
    void removeParticipant_validMember_success() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member participant = MemberFixture.createWithId("participant@example.com", "password", "nickname2", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname3", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);
        event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // when
        event.removeParticipant(participant);

        // then
        assertThat(event.getParticipants()).hasSize(1);
        assertThat(event.getParticipants().get(0).getMember()).isEqualTo(creator);
    }

    @Test
    @DisplayName("참여하지 않은 회원은 참여 취소할 수 없다")
    void removeParticipant_nonParticipant_throwsException() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member nonParticipant = MemberFixture.createWithId("non@example.com", "password", "nickname2", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname3", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);

        // when & then
        assertThatThrownBy(() -> event.removeParticipant(nonParticipant))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("참여하지 않은 회원입니다.");
    }

    @Test
    @DisplayName("참여 취소 후 다시 참여할 수 있다")
    void removeAndAddParticipant_success() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member participant = MemberFixture.createWithId("participant@example.com", "password", "nickname2", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname3", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);
        event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // when
        event.removeParticipant(participant);
        event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // then
        assertThat(event.getParticipants()).hasSize(2);
        assertThat(event.getParticipants())
            .extracting(EventParticipant::getMember)
            .contains(creator, participant);
    }

    @Test
    @DisplayName("isFull: 참여자가 최대 인원에 도달하면 true를 반환한다")
    void isFull_maxReached_returnsTrue() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member participant = MemberFixture.createWithId("participant@example.com", "password", "nickname2", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname3", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 2, LocalDate.of(2025, 1, 1), null, creator);

        // when
        event.addParticipant(participant, ParticipantRole.PARTICIPANT, java.time.LocalDateTime.now());

        // then
        assertThat(event.isFull()).isTrue();
    }

    @Test
    @DisplayName("isFull: 참여자가 최대 인원 미만이면 false를 반환한다")
    void isFull_notFull_returnsFalse() {
        // given
        Member creator = MemberFixture.createWithId("creator@example.com", "password", "nickname1", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@example.com", "password", "nickname2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", "image.png", groupLeader);

        Event event = Event.create(eventGroup, "이벤트", "설명", 10, LocalDate.of(2025, 1, 1), null, creator);

        // then
        assertThat(event.isFull()).isFalse();
    }
}
