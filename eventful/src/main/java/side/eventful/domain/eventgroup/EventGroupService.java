package side.eventful.domain.eventgroup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class EventGroupService {

    private final EventGroupRepository eventGroupRepository;
    private final MemberRepository memberRepository;

    public EventGroup create(EventGroupCommand.Create command) {
        String uniqueJoinCode = generateUniqueJoinCode();
        EventGroup eventGroup = EventGroup.createWithJoinCode(
            command.getName(),
            command.getDescription(),
            command.getImageUrl(),
            command.getLeader(),
            uniqueJoinCode
        );

        return eventGroupRepository.save(eventGroup);
    }

    private String generateUniqueJoinCode() {
        String joinCode;
        int maxAttempts = 10; // 최대 10번 시도
        int attempts = 0;

        do {
            joinCode = generateJoinCode();
            attempts++;

            if (attempts > maxAttempts) {
                throw new RuntimeException("joinCode 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        } while (eventGroupRepository.existsByJoinCode(joinCode));

        return joinCode;
    }

    private String generateJoinCode() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder code = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < 8; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    public void joinGroup(EventGroupCommand.Join command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        eventGroup.joinMember(command.getMember(), command.getGroupPassword());

        eventGroupRepository.save(eventGroup);
    }

    public void updateGroup(EventGroupCommand.Update command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        eventGroup.updateGroup(command.getName(), command.getDescription(), command.getImageUrl(), command.getRequestMember());

        eventGroupRepository.save(eventGroup);
    }

    public EventGroup getGroup(EventGroupCommand.Get command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        // 그룹 멤버인지 확인
        if (!eventGroup.getMembers().contains(command.getRequestMember())) {
            throw new IllegalArgumentException("그룹에 속하지 않은 사용자입니다");
        }

        return eventGroup;
    }

    public EventGroup verifyJoinCode(EventGroupCommand.VerifyCode command) {
        EventGroup eventGroup = eventGroupRepository.findByJoinCode(command.getJoinCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 참가 코드입니다"));

        return eventGroup;
    }

    public void removeMember(EventGroupCommand.RemoveMember command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        Member targetMember = memberRepository.findById(command.getTargetMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        eventGroup.removeMember(targetMember, command.getRequestMember());

        eventGroupRepository.save(eventGroup);
    }

    public void transferLeader(EventGroupCommand.TransferLeader command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        Member newLeader = memberRepository.findById(command.getNewLeaderMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        eventGroup.transferLeadership(newLeader, command.getRequestMember());

        eventGroupRepository.save(eventGroup);
    }

    // 그룹 삭제 처리
    public void deleteGroup(EventGroupCommand.Delete command) {
        EventGroup eventGroup = eventGroupRepository.findById(command.getEventGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다"));

        // 도메인에서 권한 검증
        eventGroup.validateDeletePermission(command.getRequestMember());

        // JPA를 통해 삭제
        eventGroupRepository.delete(eventGroup);
    }

    public java.util.List<EventGroup> getGroupList(EventGroupCommand.GetList command) {
        return eventGroupRepository.findByMember(command.getMember());
    }

    /**
     * 이벤트 생성을 위한 그룹 조회 및 권한 검증
     * 그룹이 존재하고, 요청자가 그룹원인지 확인
     *
     * @param eventGroupId 그룹 ID
     * @param member 요청자
     * @return 검증된 EventGroup
     * @throws IllegalArgumentException 그룹이 없거나 그룹원이 아닌 경우
     */
    public EventGroup getGroupForEventCreation(Long eventGroupId, Member member) {
        EventGroup eventGroup = eventGroupRepository.findById(eventGroupId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        if (!eventGroup.isMember(member)) {
            throw new IllegalArgumentException("그룹원만 이벤트를 생성할 수 있습니다.");
        }

        return eventGroup;
    }
}
