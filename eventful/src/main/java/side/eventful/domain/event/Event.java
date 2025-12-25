package side.eventful.domain.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.member.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer maxParticipants;
    private LocalDate eventDate;
    private String placeId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_group_id", nullable = false)
    private EventGroup eventGroup;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> participants = new ArrayList<>();

    private Event(EventGroup eventGroup, String name, String description, Integer maxParticipants, LocalDate eventDate, String placeId, Member creator) {
        this.eventGroup = eventGroup;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
        this.placeId = placeId;
        this.creator = creator;
    }

    public static Event create(EventGroup eventGroup, String name, String description, Integer maxParticipants, LocalDate eventDate, String placeId, Member creator) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이벤트 이름은 필수입니다.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("이벤트 설명은 필수입니다.");
        }

        if (maxParticipants != null
            && maxParticipants < 1) {
            throw new IllegalArgumentException("최대 참여자 수는 최소 1명 이상입니다.");
        }

        Event event = new Event(eventGroup, name, description, maxParticipants, eventDate, placeId, creator);

        // 생성자를 자동으로 참여자로 등록
        event.addCreatorAsParticipant(creator);

        return event;
    }

    /**
     * 이벤트 생성 시 생성자를 참여자로 등록 (내부 전용)
     * 일반 참여와 달리 중복/인원 체크를 하지 않음
     */
    private void addCreatorAsParticipant(Member creator) {
        EventParticipant creatorParticipant = EventParticipant.of(
            this,
            creator,
            ParticipantRole.CREATOR,
            LocalDateTime.now()
        );
        this.participants.add(creatorParticipant);
    }

    public boolean isFull() {
        if (maxParticipants == null) return false;
        return maxParticipants <= participants.size();
    }

    /**
     * 이벤트에 일반 회원을 참여시킨다
     *
     * <p>주의: 이 메서드는 Event 집합체 내부의 규칙만 검증합니다.
     * <strong>그룹원 여부 검증은 서비스 레이어에서 수행해야 합니다.</strong></p>
     *
     * <pre>
     * // ✅ 올바른 사용 (Service Layer)
     * Event event = eventRepository.findById(eventId).orElseThrow();
     * if (!event.getEventGroup().isMember(member)) {
     *     throw new IllegalArgumentException("그룹원만 참여 가능합니다.");
     * }
     * event.addParticipant(member, ParticipantRole.PARTICIPANT, LocalDateTime.now());
     * </pre>
     *
     * @param member 참여할 회원
     * @param role 참여자 역할 (PARTICIPANT)
     * @param joinedAt 참여 시각
     * @throws IllegalArgumentException 최대 인원 초과 또는 중복 참여 시
     */
    public void addParticipant(Member member, ParticipantRole role, LocalDateTime joinedAt) {
        if (isFull()) {
            throw new IllegalArgumentException("최대 참여 인원을 초과했습니다.");
        }

        boolean alreadyParticipating = this.participants.stream()
            .anyMatch(p -> p.getMember().equals(member));

        if (alreadyParticipating) {
            throw new IllegalArgumentException("이미 참여 중인 회원입니다.");
        }

        EventParticipant eventParticipant = EventParticipant.of(this, member, role, joinedAt);
        this.participants.add(eventParticipant);
    }

    public void removeParticipant(Member member) {
        EventParticipant target = this.participants.stream()
            .filter(participant -> participant.getMember().equals(member))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("참여하지 않은 회원입니다."));

        this.participants.remove(target);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;

        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
