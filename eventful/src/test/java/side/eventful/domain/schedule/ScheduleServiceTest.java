package side.eventful.domain.schedule;

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
import side.eventful.domain.event.ParticipantRole;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.fixture.MemberFixture;
import side.eventful.domain.member.Member;
import side.eventful.domain.vote.VoteRepository;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * ScheduleService 단위 테스트 (리팩토링 후)
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private final PasswordEncoder passwordEncoder = new TestPasswordEncoder();
    private Member creator;
    private Member groupLeader;
    private Event event;
    private EventGroup eventGroup;

    @BeforeEach
    void setUp() {
        MemberFixture.resetSequence();
        creator = MemberFixture.createWithId("creator@test.com", "password", "creator", passwordEncoder);
        groupLeader = MemberFixture.createWithId("leader@test.com", "password", "leader", passwordEncoder);
        eventGroup = EventGroup.create("테스트 그룹", "설명", null, groupLeader);
        event = Event.create(eventGroup, "제주도 여행", "2박 3일", 10, LocalDate.of(2025, 3, 15), null, creator);
    }

    @Nested
    @DisplayName("일정 생성")
    class CreateSchedule {

        @Test
        @DisplayName("이벤트 참여자가 일정을 생성할 수 있다")
        void createSchedule_validParticipant_success() {
            // given
            ScheduleCommand.CreateSchedule command = new ScheduleCommand.CreateSchedule(
                "성산일출봉 관광",
                "일출 보러 가기",
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "성산일출봉"
            );

            given(scheduleRepository.findOverlappingSchedules(any(), any(), any()))
                .willReturn(Collections.emptyList());
            given(voteRepository.findOverlappingVotes(any(), any(), any()))
                .willReturn(Collections.emptyList());
            given(scheduleRepository.save(any(Schedule.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            // when
            Schedule result = scheduleService.createSchedule(event, creator, command);

            // then
            assertThat(result.getName()).isEqualTo("성산일출봉 관광");
            assertThat(result.getMemo()).isEqualTo("일출 보러 가기");
            assertThat(result.getStartTime()).isEqualTo(LocalTime.of(9, 0));
            assertThat(result.getEndTime()).isEqualTo(LocalTime.of(11, 0));
            assertThat(result.getLocation()).isEqualTo("성산일출봉");
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("이벤트 참여자가 아니면 일정 생성에 실패한다")
        void createSchedule_notParticipant_throwsException() {
            // given
            Member nonParticipant = MemberFixture.createWithId("other@test.com", "password", "other", passwordEncoder);
            ScheduleCommand.CreateSchedule command = new ScheduleCommand.CreateSchedule(
                "성산일출봉 관광",
                "일출 보러 가기",
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "성산일출봉"
            );

            // when & then
            assertThatThrownBy(() -> scheduleService.createSchedule(event, nonParticipant, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 참여자만 일정을 생성할 수 있습니다.");
        }
    }

    @Nested
    @DisplayName("금액 입력")
    class SetAmount {

        private Schedule schedule;

        @BeforeEach
        void setUp() {
            schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소"
            );
        }

        @Test
        @DisplayName("일정 생성자가 금액을 입력할 수 있다")
        void setAmount_byCreator_success() {
            // given
            ScheduleCommand.SetAmount command = new ScheduleCommand.SetAmount(new BigDecimal("50000"));

            // when
            scheduleService.setAmount(schedule, creator, command);

            // then
            assertThat(schedule.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
        }

        @Test
        @DisplayName("그룹장이 금액을 입력할 수 있다")
        void setAmount_byGroupLeader_success() {
            // given
            ScheduleCommand.SetAmount command = new ScheduleCommand.SetAmount(new BigDecimal("50000"));

            // when
            scheduleService.setAmount(schedule, groupLeader, command);

            // then
            assertThat(schedule.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
        }

        @Test
        @DisplayName("일반 참여자는 금액을 입력할 수 없다")
        void setAmount_byOtherParticipant_throwsException() {
            // given
            Member otherParticipant = MemberFixture.createWithId("other@test.com", "password", "other", passwordEncoder);
            event.addParticipant(otherParticipant, ParticipantRole.PARTICIPANT, LocalDateTime.now());
            ScheduleCommand.SetAmount command = new ScheduleCommand.SetAmount(new BigDecimal("50000"));

            // when & then
            assertThatThrownBy(() -> scheduleService.setAmount(schedule, otherParticipant, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일정 생성자 또는 그룹장만 일정을 관리할 수 있습니다.");
        }
    }

    @Nested
    @DisplayName("영수증 파일 첨부")
    class SetReceiptFile {

        private Schedule schedule;

        @BeforeEach
        void setUp() {
            schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소"
            );
        }

        @Test
        @DisplayName("일정 생성자가 영수증을 첨부할 수 있다")
        void setReceiptFile_byCreator_success() {
            // given
            ScheduleCommand.SetReceiptFile command = new ScheduleCommand.SetReceiptFile("/receipts/receipt-123.jpg");

            // when
            scheduleService.setReceiptFile(schedule, creator, command);

            // then
            assertThat(schedule.getReceiptFilePath()).isEqualTo("/receipts/receipt-123.jpg");
        }

        @Test
        @DisplayName("그룹장이 영수증을 첨부할 수 있다")
        void setReceiptFile_byGroupLeader_success() {
            // given
            ScheduleCommand.SetReceiptFile command = new ScheduleCommand.SetReceiptFile("/receipts/receipt-123.jpg");

            // when
            scheduleService.setReceiptFile(schedule, groupLeader, command);

            // then
            assertThat(schedule.getReceiptFilePath()).isEqualTo("/receipts/receipt-123.jpg");
        }
    }

    @Nested
    @DisplayName("일정 삭제")
    class DeleteSchedule {

        private Schedule schedule;

        @BeforeEach
        void setUp() {
            schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소"
            );
        }

        @Test
        @DisplayName("일정 생성자가 일정을 삭제할 수 있다")
        void deleteSchedule_byCreator_success() {
            // when
            scheduleService.deleteSchedule(schedule, creator);

            // then
            verify(scheduleRepository).delete(schedule);
        }

        @Test
        @DisplayName("그룹장이 일정을 삭제할 수 있다")
        void deleteSchedule_byGroupLeader_success() {
            // when
            scheduleService.deleteSchedule(schedule, groupLeader);

            // then
            verify(scheduleRepository).delete(schedule);
        }
    }
}

