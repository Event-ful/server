package side.eventful.domain.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventGroupTransferLeaderTest {

    private PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @Test
    @DisplayName("그룹장 위임 - 정상 케이스")
    void transferLeader_success() {
        // given
        Member leader = Member.create("leader@example.com", "password", "리더", passwordEncoder);
        Member member = Member.create("member@example.com", "password", "멤버", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);
        eventGroup.joinMember(member, eventGroup.getJoinPassword());

        // when
        eventGroup.transferLeadership(member, leader);

        // then
        assertThat(eventGroup.getLeader()).isEqualTo(member);
        assertThat(eventGroup.isLeader(member)).isTrue();
    }

    @Test
    @DisplayName("그룹장 위임 - 요청자가 그룹장이 아닐 경우 예외")
    void transferLeader_nonLeaderRequest_throwsException() {
        // given
        Member leader = Member.create("leader@example.com", "password", "리더", passwordEncoder);
        Member member1 = Member.create("member1@example.com", "password", "멤버1", passwordEncoder);
        Member member2 = Member.create("member2@example.com", "password", "멤버2", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);
        eventGroup.joinMember(member1, eventGroup.getJoinPassword());
        eventGroup.joinMember(member2, eventGroup.getJoinPassword());

        // when & then
        assertThatThrownBy(() -> eventGroup.transferLeadership(member2, member1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹장만이 회원을 추방할 수 있습니다");
    }

    @Test
    @DisplayName("그룹장 위임 - 대상이 그룹에 속하지 않은 경우 예외")
    void transferLeader_targetNotMember_throwsException() {
        // given
        Member leader = Member.create("leader@example.com", "password", "리더", passwordEncoder);
        Member notMember = Member.create("notmember@example.com", "password", "비회원", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);

        // when & then
        assertThatThrownBy(() -> eventGroup.transferLeadership(notMember, leader))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹에 가입되지 않은 회원입니다");
    }

    @Test
    @DisplayName("그룹장 위임 - 이미 그룹장인 대상에게 위임하려 할 경우 예외")
    void transferLeader_targetIsAlreadyLeader_throwsException() {
        // given
        Member leader = Member.create("leader@example.com", "password", "리더", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);

        // when & then
        assertThatThrownBy(() -> eventGroup.transferLeadership(leader, leader))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 그룹장인 회원입니다");
    }
}

