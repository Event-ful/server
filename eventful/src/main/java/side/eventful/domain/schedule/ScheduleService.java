package side.eventful.domain.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.eventful.domain.event.Event;
import side.eventful.domain.member.Member;
import side.eventful.domain.vote.Vote;
import side.eventful.domain.vote.VoteRepository;

import java.util.List;

/**
 * 일정(Schedule) 도메인 서비스
 *
 * <p>일정과 관련된 비즈니스 로직을 처리한다:</p>
 * <ul>
 *   <li>일정 생성 (시간 중복 검증 포함)</li>
 *   <li>일정 금액 입력 (권한 검증)</li>
 *   <li>영수증 파일 첨부 (권한 검증)</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final VoteRepository voteRepository;

    /**
     * 일정 생성
     *
     * <p>일정을 생성하기 전에 다음을 검증한다:</p>
     * <ul>
     *   <li>생성자가 이벤트 참여자인지 확인</li>
     *   <li>시간이 중복되지 않는지 확인</li>
     * </ul>
     *
     * @param event 이벤트
     * @param creator 생성자
     * @param command 일정 생성 커맨드
     * @return 생성된 일정
     * @throws IllegalArgumentException 생성자가 이벤트 참여자가 아니거나 시간이 중복되는 경우
     */
    @Transactional
    public Schedule createSchedule(Event event, Member creator, ScheduleCommand.CreateSchedule command) {
        // 1. 이벤트 참여자인지 확인
        validateEventParticipant(event, creator);

        // 2. 시간 중복 검증
        validateTimeNotOverlapping(event.getId(), command.startTime(), command.endTime());

        // 3. 일정 생성
        Schedule schedule = Schedule.create(
            event,
            creator,
            command.name(),
            command.memo(),
            command.startTime(),
            command.endTime(),
            command.location()
        );

        return scheduleRepository.save(schedule);
    }

    /**
     * 이벤트 참여자인지 확인
     */
    private void validateEventParticipant(Event event, Member member) {
        boolean isParticipant = event.getParticipants().stream()
            .anyMatch(p -> p.getMember().equals(member));

        if (!isParticipant) {
            throw new IllegalArgumentException("이벤트 참여자만 일정을 생성할 수 있습니다.");
        }
    }

    /**
     * 시간 중복 검증
     * 같은 이벤트 내에서 일정 또는 투표와 시간이 겹치면 예외 발생
     */
    private void validateTimeNotOverlapping(Long eventId, java.time.LocalTime startTime, java.time.LocalTime endTime) {
        // 일정과 중복 확인
        List<Schedule> overlappingSchedules = scheduleRepository.findOverlappingSchedules(eventId, startTime, endTime);
        if (!overlappingSchedules.isEmpty()) {
            throw new IllegalArgumentException("이미 해당 시간대에 다른 일정이 존재합니다.");
        }

        // 투표와 중복 확인
        List<Vote> overlappingVotes = voteRepository.findOverlappingVotes(eventId, startTime, endTime);
        if (!overlappingVotes.isEmpty()) {
            throw new IllegalArgumentException("이미 해당 시간대에 투표가 존재합니다.");
        }
    }

    /**
     * 일정 금액 입력
     *
     * <p>일정 생성자 또는 그룹장만 금액을 입력할 수 있다.</p>
     *
     * @param schedule 일정
     * @param member 요청한 회원
     * @param command 금액 입력 커맨드
     */
    @Transactional
    public void setAmount(Schedule schedule, Member member, ScheduleCommand.SetAmount command) {
        // 권한 검증: 일정 생성자 또는 그룹장
        validateScheduleManager(schedule, member);

        schedule.setAmount(command.amount());
    }

    /**
     * 영수증 파일 첨부
     *
     * <p>일정 생성자 또는 그룹장만 영수증을 첨부할 수 있다.</p>
     *
     * @param schedule 일정
     * @param member 요청한 회원
     * @param command 파일 경로 커맨드
     */
    @Transactional
    public void setReceiptFile(Schedule schedule, Member member, ScheduleCommand.SetReceiptFile command) {
        // 권한 검증: 일정 생성자 또는 그룹장
        validateScheduleManager(schedule, member);

        schedule.setReceiptFilePath(command.filePath());
    }

    /**
     * 일정 관리 권한 검증
     * 일정 생성자 또는 그룹장만 일정을 관리(금액 입력, 영수증 첨부, 삭제 등)할 수 있다.
     */
    private void validateScheduleManager(Schedule schedule, Member member) {
        Event event = schedule.getEvent();
        boolean isCreator = schedule.isCreatedBy(member);
        boolean isGroupLeader = event.getEventGroup().isLeader(member);

        if (!isCreator && !isGroupLeader) {
            throw new IllegalArgumentException("일정 생성자 또는 그룹장만 일정을 관리할 수 있습니다.");
        }
    }

    /**
     * 일정 삭제
     *
     * <p>일정 생성자 또는 그룹장만 삭제할 수 있다.</p>
     *
     * @param schedule 삭제할 일정
     * @param member 요청한 회원
     */
    @Transactional
    public void deleteSchedule(Schedule schedule, Member member) {
        // 권한 검증: 일정 생성자 또는 그룹장
        validateScheduleManager(schedule, member);

        scheduleRepository.delete(schedule);
    }

    /**
     * 특정 이벤트의 모든 일정 조회
     */
    public List<Schedule> getSchedulesByEvent(Long eventId) {
        return scheduleRepository.findByEventId(eventId);
    }
}

