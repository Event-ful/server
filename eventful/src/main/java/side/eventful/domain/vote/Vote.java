package side.eventful.domain.vote;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.event.Event;
import side.eventful.domain.member.Member;
import side.eventful.domain.schedule.Schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 투표(Vote) 도메인 엔티티
 *
 * <p>일정의 장소를 투표로 결정하기 위한 도메인이다.</p>
 * <ul>
 *   <li>일정명, 시작/종료 시간, 메모 관리 (Schedule과 동일)</li>
 *   <li>여러 장소 옵션 관리 (VoteOption)</li>
 *   <li>참여자별 투표 기록 관리 (VoteRecord)</li>
 *   <li>투표 마감 및 Schedule로 변환</li>
 * </ul>
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

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

    // 투표 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus status;

    // 투표 옵션들 (장소 후보)
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>();

    private Vote(
        Event event,
        Member creator,
        String name,
        String memo,
        LocalTime startTime,
        LocalTime endTime
    ) {
        this.event = event;
        this.creator = creator;
        this.name = name;
        this.memo = memo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = VoteStatus.IN_PROGRESS;
    }

    /**
     * 투표 생성 팩토리 메서드
     *
     * @param event 이벤트
     * @param creator 생성자 (이벤트 참여자여야 함)
     * @param name 일정명
     * @param memo 메모
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @param locationOptions 장소 옵션들 (최소 2개 이상)
     * @return 생성된 투표
     */
    public static Vote create(
        Event event,
        Member creator,
        String name,
        String memo,
        LocalTime startTime,
        LocalTime endTime,
        List<String> locationOptions
    ) {
        // 필수 값 검증
        validateRequired(name, startTime, endTime, locationOptions);

        // 시간 유효성 검증
        validateTime(startTime, endTime);

        // 투표 옵션 개수 검증 (최소 2개)
        validateOptionCount(locationOptions);

        Vote vote = new Vote(event, creator, name, memo, startTime, endTime);

        // 투표 옵션 추가
        vote.addOptions(locationOptions);

        return vote;
    }

    /**
     * 필수 값 검증
     */
    private static void validateRequired(String name, LocalTime startTime, LocalTime endTime, List<String> locationOptions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("일정명은 필수입니다.");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("시작 시간은 필수입니다.");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("종료 시간은 필수입니다.");
        }
        if (locationOptions == null || locationOptions.isEmpty()) {
            throw new IllegalArgumentException("투표 옵션은 최소 1개 이상 입력해야 합니다.");
        }
    }

    /**
     * 시간 유효성 검증
     */
    private static void validateTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }
    }

    /**
     * 투표 옵션 개수 검증 (최소 2개 이상)
     */
    private static void validateOptionCount(List<String> locationOptions) {
        if (locationOptions.size() < 2) {
            throw new IllegalArgumentException("투표 옵션은 최소 2개 이상 입력해야 합니다.");
        }
    }

    /**
     * 투표 옵션 추가
     */
    private void addOptions(List<String> locationOptions) {
        for (String locationName : locationOptions) {
            VoteOption option = VoteOption.of(this, locationName);
            this.options.add(option);
        }
    }

    /**
     * 투표 옵션 추가 (투표 진행 중에도 가능)
     * 일정 생성자 또는 그룹장만 가능 (서비스 레이어에서 권한 검증)
     */
    public void addOption(String locationName) {
        if (this.status != VoteStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 투표만 옵션을 추가할 수 있습니다.");
        }

        VoteOption option = VoteOption.of(this, locationName);
        this.options.add(option);
    }

    /**
     * 투표 옵션 삭제
     * 일정 생성자 또는 그룹장만 가능 (서비스 레이어에서 권한 검증)
     */
    public void removeOption(Long optionId) {
        if (this.status != VoteStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 투표만 옵션을 삭제할 수 있습니다.");
        }

        VoteOption option = this.options.stream()
            .filter(o -> o.getId().equals(optionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 옵션입니다."));

        // 최소 2개는 남아야 함
        if (this.options.size() <= 2) {
            throw new IllegalArgumentException("투표 옵션은 최소 2개 이상 유지되어야 합니다.");
        }

        this.options.remove(option);
    }

    /**
     * 투표하기
     *
     * @param member 투표하는 회원
     * @param optionId 투표할 옵션 ID
     */
    public void vote(Member member, Long optionId) {
        if (this.status != VoteStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 투표만 참여할 수 있습니다.");
        }

        VoteOption option = this.options.stream()
            .filter(o -> o.getId().equals(optionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 옵션입니다."));

        // 기존 투표 취소 (재투표 가능)
        this.options.forEach(o -> o.cancelVote(member));

        // 새로운 옵션에 투표
        option.addVote(member);
    }

    /**
     * 투표 마감
     * 일정 생성자 또는 그룹장만 가능 (서비스 레이어에서 권한 검증)
     */
    public void close() {
        if (this.status != VoteStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 마감된 투표입니다.");
        }

        this.status = VoteStatus.CLOSED;
    }

    /**
     * 최다 득표 옵션 조회
     *
     * @return 최다 득표한 장소명
     */
    public String getWinningLocation() {
        if (this.status != VoteStatus.CLOSED) {
            throw new IllegalStateException("투표가 마감되어야 결과를 확인할 수 있습니다.");
        }

        return this.options.stream()
            .max((o1, o2) -> Integer.compare(o1.getVoteCount(), o2.getVoteCount()))
            .map(VoteOption::getLocationName)
            .orElseThrow(() -> new IllegalStateException("투표 옵션이 존재하지 않습니다."));
    }

    /**
     * 투표 결과 집계
     *
     * @return 장소명 -> 득표수 맵
     */
    public Map<String, Integer> getVoteResults() {
        return this.options.stream()
            .collect(Collectors.toMap(
                VoteOption::getLocationName,
                VoteOption::getVoteCount
            ));
    }

    /**
     * Schedule로 변환
     * 투표 마감 후에만 가능
     *
     * @return 생성된 Schedule
     */
    public Schedule toSchedule() {
        if (this.status != VoteStatus.CLOSED) {
            throw new IllegalStateException("투표가 마감되어야 일정으로 변환할 수 있습니다.");
        }

        String winningLocation = getWinningLocation();

        return Schedule.create(
            this.event,
            this.creator,
            this.name,
            this.memo,
            this.startTime,
            this.endTime,
            winningLocation
        );
    }

    /**
     * 시간 중복 여부 확인
     * @param otherStartTime
     * @param otherEndTime
     * @return
     */
    public boolean isTimeOverlapping(LocalTime otherStartTime, LocalTime otherEndTime) {
        boolean startOverlaps = !this.startTime.isBefore(otherStartTime)
                                && this.startTime.isBefore(otherEndTime);

        boolean endOverlaps = this.endTime.isAfter(otherStartTime)
                              && !this.endTime.isAfter(otherEndTime);

        boolean contains = !this.startTime.isAfter(otherStartTime)
                          && !this.endTime.isBefore(otherEndTime);

        return startOverlaps || endOverlaps || contains;
    }

    /**
     * 생성자인지 확인
     */
    public boolean isCreatedBy(Member member) {
        return this.creator.equals(member);
    }

    /**
     * 진행 중인지 확인
     */
    public boolean isInProgress() {
        return this.status == VoteStatus.IN_PROGRESS;
    }

    /**
     * 마감되었는지 확인
     */
    public boolean isClosed() {
        return this.status == VoteStatus.CLOSED;
    }
}

