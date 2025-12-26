package side.eventful.domain.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.event.Event;
import side.eventful.domain.member.Member;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 일정(Schedule) 도메인 엔티티
 *
 * <p>확정된 일정을 관리한다. 투표를 통해 확정된 일정이거나, 직접 생성한 일정이다.</p>
 * <ul>
 *   <li>일정명, 시작/종료 시간, 메모, 확정된 장소 관리</li>
 *   <li>시간 유효성 검증 (종료 시간 > 시작 시간)</li>
 *   <li>금액 및 영수증 파일 경로 관리</li>
 * </ul>
 *
 * <p>투표 기능은 {@link side.eventful.domain.vote.Vote} 도메인에서 처리한다.</p>
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String memo;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // 확정된 장소 (1개)
    @Column(nullable = false)
    private String location;

    // 일정 금액 (선택)
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    // 영수증 파일 경로 (선택)
    private String receiptFilePath;

    private Schedule(
        Event event,
        Member creator,
        String name,
        String memo,
        LocalTime startTime,
        LocalTime endTime,
        String location
    ) {
        this.event = event;
        this.creator = creator;
        this.name = name;
        this.memo = memo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }


    /**
     * 일정 생성 팩토리 메서드
     *
     * @param event 이벤트
     * @param creator 생성자 (이벤트 참여자여야 함 - 서비스 레이어에서 검증)
     * @param name 일정명
     * @param memo 메모
     * @param startTime 시작 시간 (HH:mm)
     * @param endTime 종료 시간 (HH:mm)
     * @param location 확정된 장소
     * @return 생성된 일정
     */
    public static Schedule create(
        Event event,
        Member creator,
        String name,
        String memo,
        LocalTime startTime,
        LocalTime endTime,
        String location
    ) {
        // 필수 값 검증
        validateRequired(name, startTime, endTime, location);

        // 시간 유효성 검증
        validateTime(startTime, endTime);

        return new Schedule(event, creator, name, memo, startTime, endTime, location);
    }

    /**
     * 필수 값 검증
     */
    private static void validateRequired(String name, LocalTime startTime, LocalTime endTime, String location) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("일정명은 필수입니다.");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("시작 시간은 필수입니다.");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("종료 시간은 필수입니다.");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("장소는 필수입니다.");
        }
    }

    /**
     * 시간 유효성 검증
     * 종료 시간은 시작 시간보다 빠를 수 없다.
     * (23:00 ~ 01:00 같은 자정 넘김은 현재 불가)
     */
    private static void validateTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }
    }

    /**
     * 일정 금액 입력
     * 일정 생성자 또는 그룹장만 가능 (서비스 레이어에서 권한 검증)
     */
    public void setAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
        }
        this.amount = amount;
    }

    /**
     * 영수증 파일 경로 설정
     * 일정 생성자 또는 그룹장만 가능 (서비스 레이어에서 권한 검증)
     */
    public void setReceiptFilePath(String filePath) {
        this.receiptFilePath = filePath;
    }

    /**
     * 시간 중복 여부 확인
     *
     * @param other 비교할 다른 일정
     * @return 시간이 겹치면 true
     */
    public boolean isTimeOverlapping(Schedule other) {
        // 시작 시간이 다른 일정의 시간 범위에 포함되는지 확인
        boolean startOverlaps = !this.startTime.isBefore(other.startTime)
                                && this.startTime.isBefore(other.endTime);

        // 종료 시간이 다른 일정의 시간 범위에 포함되는지 확인
        boolean endOverlaps = this.endTime.isAfter(other.startTime)
                              && !this.endTime.isAfter(other.endTime);

        // 다른 일정을 완전히 포함하는 경우
        boolean contains = !this.startTime.isAfter(other.startTime)
                          && !this.endTime.isBefore(other.endTime);

        return startOverlaps || endOverlaps || contains;
    }

    /**
     * 생성자인지 확인
     */
    public boolean isCreatedBy(Member member) {
        return this.creator.equals(member);
    }
}

