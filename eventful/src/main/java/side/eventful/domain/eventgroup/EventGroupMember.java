package side.eventful.domain.eventgroup;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.member.Member;

@Entity
@Table(name = "event_group_member")
@Getter
@NoArgsConstructor
public class EventGroupMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_group_id", nullable = false)
    private EventGroup eventGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private EventGroupMember(EventGroup eventGroup, Member member) {
        this.eventGroup = eventGroup;
        this.member = member;
    }

    public static EventGroupMember of(EventGroup eventGroup, Member member) {
        return new EventGroupMember(eventGroup, member);
    }
}
