package side.eventful.domain.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.member.Member;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class EventParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantRole role;

    private LocalDateTime joinedAt;

    private EventParticipant(Event event, Member member, ParticipantRole role, LocalDateTime joinedAt) {
        this.event = event;
        this.member = member;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public static EventParticipant of(Event event, Member member, ParticipantRole role, LocalDateTime joinedAt) {
        return new EventParticipant(event, member, role, joinedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventParticipant that)) return false;

        return event != null
            && member != null
            && event.equals(that.event)
            && member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, member);
    }
}
