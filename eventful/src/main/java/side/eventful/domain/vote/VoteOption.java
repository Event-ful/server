package side.eventful.domain.vote;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * 투표 옵션(VoteOption) 엔티티
 *
 * <p>투표에서 선택할 수 있는 장소 옵션을 나타낸다.</p>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(nullable = false)
    private String locationName;

    // 이 옵션에 대한 투표 기록
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteRecord> voteRecords = new ArrayList<>();

    private VoteOption(Vote vote, String locationName) {
        this.vote = vote;
        this.locationName = locationName;
    }

    public static VoteOption of(Vote vote, String locationName) {
        if (locationName == null || locationName.isBlank()) {
            throw new IllegalArgumentException("장소명은 필수입니다.");
        }
        return new VoteOption(vote, locationName);
    }

    /**
     * 투표 추가
     *
     * @param member 투표하는 회원
     */
    public void addVote(Member member) {
        // 이미 투표했는지 확인
        boolean alreadyVoted = this.voteRecords.stream()
            .anyMatch(record -> record.getMember().equals(member));

        if (!alreadyVoted) {
            VoteRecord record = VoteRecord.of(this, member);
            this.voteRecords.add(record);
        }
    }

    /**
     * 투표 취소
     *
     * @param member 취소할 회원
     */
    public void cancelVote(Member member) {
        this.voteRecords.removeIf(record -> record.getMember().equals(member));
    }

    /**
     * 득표수 조회
     *
     * @return 득표수
     */
    public int getVoteCount() {
        return this.voteRecords.size();
    }
}

