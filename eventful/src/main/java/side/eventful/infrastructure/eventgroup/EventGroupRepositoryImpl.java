package side.eventful.infrastructure.eventgroup;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.eventgroup.EventGroupRepository;
import side.eventful.domain.member.Member;

@Repository
@RequiredArgsConstructor
public class EventGroupRepositoryImpl implements EventGroupRepository {

    private final EventGroupJpaRepository eventGroupJpaRepository;

    @Override
    public EventGroup save(EventGroup eventGroup) {
        return eventGroupJpaRepository.save(eventGroup);
    }

    @Override
    public Optional<EventGroup> findById(Long id) {
        return eventGroupJpaRepository.findById(id);
    }

    @Override
    public Optional<EventGroup> findByJoinCode(String joinCode) {
        return eventGroupJpaRepository.findByJoinCode(joinCode);
    }

    @Override
    public boolean existsByJoinCode(String joinCode) {
        return eventGroupJpaRepository.existsByJoinCode(joinCode);
    }

    @Override
    public List<EventGroup> findByMember(Member member) {
        return eventGroupJpaRepository.findByMember(member);
    }
}
