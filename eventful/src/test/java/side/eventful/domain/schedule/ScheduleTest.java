package side.eventful.domain.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.event.Event;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.fixture.MemberFixture;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Schedule 도메인 엔티티 테스트 (리팩토링 후)
 *
 * <p>확정된 일정만 관리하도록 간소화됨</p>
 */
class ScheduleTest {

    private final PasswordEncoder passwordEncoder = new TestPasswordEncoder();
    private Member creator;
    private Event event;

    @BeforeEach
    void setUp() {
        MemberFixture.resetSequence();
        creator = MemberFixture.createWithId("creator@test.com", "password", "creator", passwordEncoder);
        Member groupLeader = MemberFixture.createWithId("leader@test.com", "password", "leader", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("테스트 그룹", "설명", null, groupLeader);
        event = Event.create(eventGroup, "제주도 여행", "2박 3일", 10, LocalDate.of(2025, 3, 15), null, creator);
    }

    @Nested
    @DisplayName("일정 생성")
    class CreateSchedule {

        @Test
        @DisplayName("일정을 생성할 수 있다")
        void create_validInput_success() {
            // given
            String name = "성산일출봉 관광";
            String memo = "일출 보러 가기";
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(11, 0);
            String location = "성산일출봉";

            // when
            Schedule schedule = Schedule.create(event, creator, name, memo, startTime, endTime, location);

            // then
            assertThat(schedule.getName()).isEqualTo(name);
            assertThat(schedule.getMemo()).isEqualTo(memo);
            assertThat(schedule.getStartTime()).isEqualTo(startTime);
            assertThat(schedule.getEndTime()).isEqualTo(endTime);
            assertThat(schedule.getLocation()).isEqualTo(location);
        }

        @Test
        @DisplayName("일정명이 null이면 예외가 발생한다")
        void create_withNullName_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, null, "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "성산일출봉"
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일정명은 필수입니다.");
        }

        @Test
        @DisplayName("일정명이 빈 문자열이면 예외가 발생한다")
        void create_withBlankName_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "   ", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "성산일출봉"
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일정명은 필수입니다.");
        }

        @Test
        @DisplayName("시작 시간이 null이면 예외가 발생한다")
        void create_withNullStartTime_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "일정", "메모",
                null, LocalTime.of(11, 0), "성산일출봉"
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 시간은 필수입니다.");
        }

        @Test
        @DisplayName("종료 시간이 null이면 예외가 발생한다")
        void create_withNullEndTime_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), null, "성산일출봉"
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료 시간은 필수입니다.");
        }

        @Test
        @DisplayName("종료 시간이 시작 시간보다 빠르면 예외가 발생한다")
        void create_withEndTimeBeforeStartTime_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(11, 0), LocalTime.of(9, 0), "성산일출봉"
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료 시간은 시작 시간보다 이후여야 합니다.");
        }

        @Test
        @DisplayName("종료 시간이 시작 시간과 같으면 예외가 발생한다")
        void create_withSameStartAndEndTime_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(9, 0), "성산일출봉"
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료 시간은 시작 시간보다 이후여야 합니다.");
        }

        @Test
        @DisplayName("장소가 null이면 예외가 발생한다")
        void create_withNullLocation_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("장소는 필수입니다.");
        }

        @Test
        @DisplayName("장소가 빈 문자열이면 예외가 발생한다")
        void create_withBlankLocation_throwsException() {
            // when & then
            assertThatThrownBy(() -> Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "   "
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("장소는 필수입니다.");
        }
    }

    @Nested
    @DisplayName("금액 설정")
    class SetAmount {

        private Schedule schedule;

        @BeforeEach
        void setUp() {
            schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "성산일출봉"
            );
        }

        @Test
        @DisplayName("금액을 설정할 수 있다")
        void setAmount_validAmount_success() {
            // given
            BigDecimal amount = new BigDecimal("50000");

            // when
            schedule.setAmount(amount);

            // then
            assertThat(schedule.getAmount()).isEqualByComparingTo(amount);
        }

        @Test
        @DisplayName("금액을 0으로 설정할 수 있다")
        void setAmount_zeroAmount_success() {
            // given
            BigDecimal amount = BigDecimal.ZERO;

            // when
            schedule.setAmount(amount);

            // then
            assertThat(schedule.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("음수 금액을 설정하면 예외가 발생한다")
        void setAmount_negativeAmount_throwsException() {
            // given
            BigDecimal amount = new BigDecimal("-1000");

            // when & then
            assertThatThrownBy(() -> schedule.setAmount(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("금액은 0 이상이어야 합니다.");
        }

        @Test
        @DisplayName("null 금액을 설정할 수 있다")
        void setAmount_nullAmount_success() {
            // given
            schedule.setAmount(new BigDecimal("50000"));

            // when
            schedule.setAmount(null);

            // then
            assertThat(schedule.getAmount()).isNull();
        }
    }

    @Nested
    @DisplayName("영수증 파일 설정")
    class SetReceiptFile {

        private Schedule schedule;

        @BeforeEach
        void setUp() {
            schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "성산일출봉"
            );
        }

        @Test
        @DisplayName("영수증 파일 경로를 설정할 수 있다")
        void setReceiptFile_validPath_success() {
            // given
            String filePath = "/receipts/2025/03/receipt-123.jpg";

            // when
            schedule.setReceiptFilePath(filePath);

            // then
            assertThat(schedule.getReceiptFilePath()).isEqualTo(filePath);
        }
    }

    @Nested
    @DisplayName("시간 중복 확인")
    class IsTimeOverlapping {

        @Test
        @DisplayName("시간이 완전히 겹치면 true를 반환한다")
        void isTimeOverlapping_exactSameTime_returnsTrue() {
            // given
            Schedule schedule1 = Schedule.create(
                event, creator, "일정1", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소1"
            );
            Schedule schedule2 = Schedule.create(
                event, creator, "일정2", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소2"
            );

            // when & then
            assertThat(schedule1.isTimeOverlapping(schedule2)).isTrue();
        }

        @Test
        @DisplayName("시간이 전혀 겹치지 않으면 false를 반환한다")
        void isTimeOverlapping_noOverlap_returnsFalse() {
            // given
            Schedule schedule1 = Schedule.create(
                event, creator, "일정1", "메모",
                LocalTime.of(8, 0), LocalTime.of(9, 0), "장소1"
            );
            Schedule schedule2 = Schedule.create(
                event, creator, "일정2", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소2"
            );

            // when & then
            assertThat(schedule1.isTimeOverlapping(schedule2)).isFalse();
        }
    }

    @Nested
    @DisplayName("생성자 확인")
    class IsCreatedBy {

        @Test
        @DisplayName("생성자가 맞으면 true를 반환한다")
        void isCreatedBy_creator_returnsTrue() {
            // given
            Schedule schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소"
            );

            // when & then
            assertThat(schedule.isCreatedBy(creator)).isTrue();
        }

        @Test
        @DisplayName("생성자가 아니면 false를 반환한다")
        void isCreatedBy_notCreator_returnsFalse() {
            // given
            Member otherMember = MemberFixture.createWithId("other@test.com", "password", "other", passwordEncoder);
            Schedule schedule = Schedule.create(
                event, creator, "일정", "메모",
                LocalTime.of(9, 0), LocalTime.of(11, 0), "장소"
            );

            // when & then
            assertThat(schedule.isCreatedBy(otherMember)).isFalse();
        }
    }
}

