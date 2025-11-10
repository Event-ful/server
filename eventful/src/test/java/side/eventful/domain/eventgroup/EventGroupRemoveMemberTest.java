package side.eventful.domain.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.MemberRepository;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventGroupRemoveMemberTest {

    @Mock
    private EventGroupRepository eventGroupRepository;

    @Mock
    private MemberRepository memberRepository;

    private PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @InjectMocks
    private EventGroupService eventGroupService;

    @Test
    @DisplayName("그룹 멤버 삭제 - 정상 케이스")
    void removeMember_withValidInput_success() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        Member targetMember = Member.create("target@test.com", "password", "추방대상", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);
        eventGroup.joinMember(targetMember, eventGroup.getJoinPassword());

        given(eventGroupRepository.findById(1L))
                .willReturn(Optional.of(eventGroup));
        given(memberRepository.findById(2L))
                .willReturn(Optional.of(targetMember));
        given(eventGroupRepository.save(any(EventGroup.class)))
                .willReturn(eventGroup);

        EventGroupCommand.RemoveMember command = EventGroupCommand.RemoveMember.create(1L, 2L, leader);

        // when
        eventGroupService.removeMember(command);

        // then
        verify(eventGroupRepository).findById(1L);
        verify(memberRepository).findById(2L);
        verify(eventGroupRepository).save(eventGroup);
    }

    @Test
    @DisplayName("그룹 멤버 삭제 - 존재하지 않는 그룹")
    void removeMember_withNonExistentGroup_throwsException() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        EventGroupCommand.RemoveMember command = EventGroupCommand.RemoveMember.create(999L, 2L, leader);

        given(eventGroupRepository.findById(999L))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.removeMember(command));
        verify(eventGroupRepository).findById(999L);
    }

    @Test
    @DisplayName("그룹 멤버 삭제 - 존재하지 않는 회원")
    void removeMember_withNonExistentMember_throwsException() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);

        given(eventGroupRepository.findById(1L))
                .willReturn(Optional.of(eventGroup));
        given(memberRepository.findById(999L))
                .willReturn(Optional.empty());

        EventGroupCommand.RemoveMember command = EventGroupCommand.RemoveMember.create(1L, 999L, leader);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.removeMember(command));
        verify(eventGroupRepository).findById(1L);
        verify(memberRepository).findById(999L);
    }

    @Test
    @DisplayName("그룹 멤버 삭제 - 그룹장이 아닌 멤버가 요청하면 도메인 오류")
    void removeMember_withNonLeaderRequest_throwsException() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        Member normalMember = Member.create("member@test.com", "password", "일반멤버", passwordEncoder);
        Member targetMember = Member.create("target@test.com", "password", "추방대상", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);
        eventGroup.joinMember(normalMember, eventGroup.getJoinPassword());
        eventGroup.joinMember(targetMember, eventGroup.getJoinPassword());

        given(eventGroupRepository.findById(1L))
                .willReturn(Optional.of(eventGroup));
        given(memberRepository.findById(3L))
                .willReturn(Optional.of(targetMember));

        EventGroupCommand.RemoveMember command = EventGroupCommand.RemoveMember.create(1L, 3L, normalMember);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.removeMember(command));
    }

    @Test
    @DisplayName("그룹 멤버 삭제 - 그룹장을 추방하려고 하면 도메인 오류")
    void removeMember_targetIsLeader_throwsException() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);

        given(eventGroupRepository.findById(1L))
                .willReturn(Optional.of(eventGroup));
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(leader));

        EventGroupCommand.RemoveMember command = EventGroupCommand.RemoveMember.create(1L, 1L, leader);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.removeMember(command));
    }
}

