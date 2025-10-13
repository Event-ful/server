package side.eventful.infrastructure.eventgroup;

import org.springframework.data.jpa.repository.JpaRepository;
import side.eventful.domain.eventgroup.EventGroup;

public interface EventGroupJpaRepository extends JpaRepository<EventGroup, Long> {
}
