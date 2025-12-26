package side.eventful.domain.vote;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.eventful.domain.common.BaseEntity;
import side.eventful.domain.member.Member;

/**
 * 투표 기록(VoteRecord) 엔티티
 *
 * <p>누가 어떤 옵션에 투표했는지 기록한다.</p>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_option_id", nullable = false)
    private VoteOption option;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private VoteRecord(VoteOption option, Member member) {
        this.option = option;
        this.member = member;
    }

    public static VoteRecord of(VoteOption option, Member member) {
        return new VoteRecord(option, member);
    }
}

