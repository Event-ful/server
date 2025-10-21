package side.eventful.domain.eventgroup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventGroupService {

    private final EventGroupRepository eventGroupRepository;

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
}
