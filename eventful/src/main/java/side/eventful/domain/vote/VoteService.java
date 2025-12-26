package side.eventful.domain.vote;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.eventful.domain.event.Event;
import side.eventful.domain.member.Member;
import side.eventful.domain.schedule.Schedule;
import side.eventful.domain.schedule.ScheduleRepository;

import java.time.LocalTime;
import java.util.List;

/**
 * 투표(Vote) 도메인 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 투표 생성
     *
     * @param event 이벤트
     * @param creator 생성자
     * @param command 투표 생성 커맨드
     * @return 생성된 투표
     */
    @Transactional
    public Vote createVote(Event event, Member creator, VoteCommand.CreateVote command) {
        // 1. 이벤트 참여자인지 확인
        validateEventParticipant(event, creator);

        // 2. 시간 중복 검증 (일정 + 투표)
        validateTimeNotOverlapping(event.getId(), command.startTime(), command.endTime());

        // 3. 투표 생성
        Vote vote = Vote.create(
            event,
            creator,
            command.name(),
            command.memo(),
            command.startTime(),
            command.endTime(),
            command.locationOptions()
        );

        return voteRepository.save(vote);
    }

    /**
     * 투표 옵션 추가
     *
     * @param vote 투표
     * @param member 요청한 회원
     * @param command 옵션 추가 커맨드
     */
    @Transactional
    public void addOption(Vote vote, Member member, VoteCommand.AddOption command) {
        // 권한 검증: 투표 생성자 또는 그룹장
        validateVoteManager(vote, member);

        vote.addOption(command.locationName());
    }

    /**
     * 투표 옵션 삭제
     *
     * @param vote 투표
     * @param member 요청한 회원
     * @param optionId 삭제할 옵션 ID
     */
    @Transactional
    public void removeOption(Vote vote, Member member, Long optionId) {
        // 권한 검증: 투표 생성자 또는 그룹장
        validateVoteManager(vote, member);

        vote.removeOption(optionId);
    }

    /**
     * 투표하기
     *
     * @param vote 투표
     * @param member 투표하는 회원
     * @param command 투표 커맨드
     */
    @Transactional
    public void castVote(Vote vote, Member member, VoteCommand.CastVote command) {
        // 이벤트 참여자인지 확인
        validateEventParticipant(vote.getEvent(), member);

        vote.vote(member, command.optionId());
    }

    /**
     * 투표 마감 및 일정으로 변환
     *
     * @param vote 투표
     * @param member 요청한 회원
     * @return 생성된 일정
     */
    @Transactional
    public Schedule closeVoteAndCreateSchedule(Vote vote, Member member) {
        // 권한 검증: 투표 생성자 또는 그룹장
        validateVoteManager(vote, member);

        // 투표 마감
        vote.close();

        // Schedule로 변환
        Schedule schedule = vote.toSchedule();

        return scheduleRepository.save(schedule);
    }

    /**
     * 투표 삭제
     *
     * @param vote 삭제할 투표
     * @param member 요청한 회원
     */
    @Transactional
    public void deleteVote(Vote vote, Member member) {
        // 권한 검증: 투표 생성자 또는 그룹장
        validateVoteManager(vote, member);

        voteRepository.delete(vote);
    }

    /**
     * 이벤트별 투표 조회
     */
    public List<Vote> getVotesByEvent(Long eventId) {
        return voteRepository.findByEventId(eventId);
    }

    /**
     * 진행 중인 투표만 조회
     */
    public List<Vote> getInProgressVotes(Long eventId) {
        return voteRepository.findInProgressVotesByEventId(eventId);
    }

    /**
     * 이벤트 참여자인지 확인
     */
    private void validateEventParticipant(Event event, Member member) {
        boolean isParticipant = event.getParticipants().stream()
            .anyMatch(p -> p.getMember().equals(member));

        if (!isParticipant) {
            throw new IllegalArgumentException("이벤트 참여자만 투표를 생성하거나 참여할 수 있습니다.");
        }
    }

    /**
     * 시간 중복 검증
     * 같은 이벤트 내에서 일정 또는 투표와 시간이 겹치지 않아야 함
     */
    private void validateTimeNotOverlapping(Long eventId, LocalTime startTime, LocalTime endTime) {
        // 일정과 중복 확인
        List<Schedule> overlappingSchedules = scheduleRepository.findOverlappingSchedules(eventId, startTime, endTime);
        if (!overlappingSchedules.isEmpty()) {
            throw new IllegalArgumentException("이미 해당 시간대에 다른 일정이 존재합니다.");
        }

        // 투표와 중복 확인
        List<Vote> overlappingVotes = voteRepository.findOverlappingVotes(eventId, startTime, endTime);
        if (!overlappingVotes.isEmpty()) {
            throw new IllegalArgumentException("이미 해당 시간대에 다른 투표가 존재합니다.");
        }
    }

    /**
     * 투표 관리 권한 검증
     * 투표 생성자 또는 그룹장만 투표를 관리할 수 있다.
     */
    private void validateVoteManager(Vote vote, Member member) {
        Event event = vote.getEvent();
        boolean isCreator = vote.isCreatedBy(member);
        boolean isGroupLeader = event.getEventGroup().isLeader(member);

        if (!isCreator && !isGroupLeader) {
            throw new IllegalArgumentException("투표 생성자 또는 그룹장만 투표를 관리할 수 있습니다.");
        }
    }
}

