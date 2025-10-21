package side.eventful.domain.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventGroupServiceTest {

    @Mock
    private EventGroupRepository eventGroupRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EventGroupService eventGroupService;

    @Test
    @DisplayName("그룹 생성 - 정상 케이스")
    void create_withValidCommand_returnsCreatedEventGroup() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("제주도 여행", "2박 3일 여행", "https://example.com/image.jpg", leader);

        EventGroup expectedEventGroup = EventGroup.create(command.getName(), command.getDescription(), command.getImageUrl(), command.getLeader());
        given(eventGroupRepository.save(any(EventGroup.class)))
                .willReturn(expectedEventGroup);

        // when
        EventGroup result = eventGroupService.create(command);

        // then
        assertThat(result).isEqualTo(expectedEventGroup);
        verify(eventGroupRepository).save(any(EventGroup.class));
    }

    @Test
    @DisplayName("그룹 생성 - null 명령어")
    void create_withNullCommand_throwsException() {
        // given
        EventGroupCommand.Create command = null;

        // when, then
        assertThrows(NullPointerException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 생성 - 저장소 오류")
    void create_withRepositoryError_throwsException() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("제주도 여행", "2박 3일 여행", "https://example.com/image.jpg", leader);

        given(eventGroupRepository.save(any(EventGroup.class)))
                .willThrow(new RuntimeException("데이터베이스 오류"));

        // when, then
        assertThrows(RuntimeException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 생성 - 유효하지 않은 그룹 이름으로 도메인 오류")
    void create_withInvalidGroupName_throwsException() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create(null, "설명", "https://example.com/image.jpg", leader);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 생성 - 그룹 이름이 15자 초과로 도메인 오류")
    void create_withGroupNameExceeding15Characters_throwsException() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        String longName = "매우긴그룹이름입니다열다섯자넘어요";
        EventGroupCommand.Create command = EventGroupCommand.Create.create(longName, "설명", "https://example.com/image.jpg", leader);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 생성 - 그룹 설명이 200자 초과로 도메인 오류")
    void create_withGroupDescriptionExceeding200Characters_throwsException() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        String longDescription = "가".repeat(201);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("그룹명", longDescription, "https://example.com/image.jpg", leader);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 생성 - null 그룹장으로 도메인 오류")
    void create_withNullLeader_throwsException() {
        // given
        EventGroupCommand.Create command = EventGroupCommand.Create.create("그룹명", "설명", "https://example.com/image.jpg", null);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 생성 - 빈 그룹 이름으로 도메인 오류")
    void create_withEmptyGroupName_throwsException() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("", "설명", "https://example.com/image.jpg", leader);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.create(command));
    }

    @Test
    @DisplayName("그룹 참여 - 정상 케이스")
    void joinGroup_withValidInput_success() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        Member member = Member.create("member@test.com", "password", "참여자", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);

        given(eventGroupRepository.findById(1L))
                .willReturn(Optional.of(eventGroup));
        given(eventGroupRepository.save(any(EventGroup.class)))
                .willReturn(eventGroup);

        EventGroupCommand.Join command = EventGroupCommand.Join.create(1L, member, eventGroup.getJoinPassword());

        // when
        eventGroupService.joinGroup(command);

        // then
        verify(eventGroupRepository).findById(1L);
        verify(eventGroupRepository).save(eventGroup);
    }

    @Test
    @DisplayName("그룹 참여 - 존재하지 않는 그룹")
    void joinGroup_withNonExistentGroup_throwsException() {
        // given
        Member member = Member.create("member@test.com", "password", "참여자", passwordEncoder);
        EventGroupCommand.Join command = EventGroupCommand.Join.create(999L, member, "password");

        given(eventGroupRepository.findById(999L))
                .willReturn(Optional.empty());

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.joinGroup(command));
        verify(eventGroupRepository).findById(999L);
    }

    @Test
    @DisplayName("그룹 참여 - 잘못된 비밀번호로 도메인 오류")
    void joinGroup_withWrongPassword_throwsException() {
        // given
        Member leader = Member.create("leader@test.com", "password", "그룹장", passwordEncoder);
        Member member = Member.create("member@test.com", "password", "참여자", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("소모임", "설명", "https://example.com/image.jpg", leader);

        given(eventGroupRepository.findById(1L))
                .willReturn(Optional.of(eventGroup));

        EventGroupCommand.Join command = EventGroupCommand.Join.create(1L, member, "wrongpass");

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupService.joinGroup(command));
    }

    private Member createTestMember() {
        return Member.create("test@example.com", "password", "테스터", passwordEncoder);
    }
}
