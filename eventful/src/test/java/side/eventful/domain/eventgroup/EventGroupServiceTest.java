package side.eventful.domain.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

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

    private PasswordEncoder passwordEncoder = new TestPasswordEncoder();

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

    @Test
    @DisplayName("유효한 joinCode로 그룹 정보를 조회할 수 있다")
    void verifyJoinCode_WithValidCode_ShouldReturnEventGroup() {
        // given
        String joinCode = "ABC12345";
        Member leader = Member.create("test@example.com", "password", "TestUser", passwordEncoder);
        EventGroup eventGroup = EventGroup.create("TestGroup", "Test Description", null, leader);

        EventGroupCommand.VerifyCode command = EventGroupCommand.VerifyCode.create(joinCode);

        given(eventGroupRepository.findByJoinCode(joinCode)).willReturn(Optional.of(eventGroup));

        // when
        EventGroup result = eventGroupService.verifyJoinCode(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestGroup");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getJoinCode()).isNotNull();
        assertThat(result.getJoinCode()).hasSize(8);
    }

    @Test
    @DisplayName("유효하지 않은 joinCode로 조회 시 예외가 발생한다")
    void verifyJoinCode_WithInvalidCode_ShouldThrowException() {
        // given
        String invalidJoinCode = "INVALID1";

        EventGroupCommand.VerifyCode command = EventGroupCommand.VerifyCode.create(invalidJoinCode);

        given(eventGroupRepository.findByJoinCode(invalidJoinCode)).willReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventGroupService.verifyJoinCode(command)
        );

        assertThat(exception.getMessage()).isEqualTo("유효하지 않은 참가 코드입니다");
    }

    @Test
    @DisplayName("존재하지 않는 그룹 ID로 joinCode 검증 시 예외가 발생한다")
    void verifyJoinCode_WithNonExistentGroup_ShouldThrowException() {
        // given
        String nonExistentJoinCode = "NOGROUP1";

        EventGroupCommand.VerifyCode command = EventGroupCommand.VerifyCode.create(nonExistentJoinCode);

        given(eventGroupRepository.findByJoinCode(nonExistentJoinCode)).willReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventGroupService.verifyJoinCode(command)
        );

        assertThat(exception.getMessage()).isEqualTo("유효하지 않은 참가 코드입니다");
    }

    @Test
    @DisplayName("그룹 생성 - joinCode 중복 체크 후 유니크한 코드로 생성")
    void create_withDuplicateJoinCode_shouldRetryAndCreateWithUniqueCode() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("제주도 여행", "2박 3일 여행", "https://example.com/image.jpg", leader);

        // 첫 번째 joinCode는 중복, 두 번째는 유니크하다고 가정
        given(eventGroupRepository.existsByJoinCode(any(String.class)))
                .willReturn(true)   // 첫 번째 시도 - 중복
                .willReturn(false); // 두 번째 시도 - 유니크

        EventGroup expectedEventGroup = EventGroup.createWithJoinCode(
            command.getName(), command.getDescription(), command.getImageUrl(), command.getLeader(), "UNIQUE12"
        );
        given(eventGroupRepository.save(any(EventGroup.class)))
                .willReturn(expectedEventGroup);

        // when
        EventGroup result = eventGroupService.create(command);

        // then
        assertThat(result).isEqualTo(expectedEventGroup);
        verify(eventGroupRepository, org.mockito.Mockito.times(2)).existsByJoinCode(any(String.class));
        verify(eventGroupRepository).save(any(EventGroup.class));
    }

    @Test
    @DisplayName("그룹 생성 - joinCode 생성 최대 재시도 횟수 초과 시 예외 발생")
    void create_withMaxAttemptsExceeded_shouldThrowException() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("제주도 여행", "2박 3일 여행", "https://example.com/image.jpg", leader);

        // 모든 시도에서 중복이라고 가정 (11번 호출: 10번 재시도 + 1번 초기 시도)
        given(eventGroupRepository.existsByJoinCode(any(String.class)))
                .willReturn(true);

        // when & then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> eventGroupService.create(command)
        );

        assertThat(exception.getMessage()).isEqualTo("joinCode 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
        verify(eventGroupRepository, org.mockito.Mockito.times(10)).existsByJoinCode(any(String.class));
        verify(eventGroupRepository, org.mockito.Mockito.never()).save(any(EventGroup.class));
    }

    @Test
    @DisplayName("그룹 생성 - 첫 번째 시도에서 유니크한 joinCode 생성 성공")
    void create_withFirstAttemptSuccess_shouldCreateWithoutRetry() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("제주도 여행", "2박 3일 여행", "https://example.com/image.jpg", leader);

        // 첫 번째 시도에서 바로 유니크한 코드 생성
        given(eventGroupRepository.existsByJoinCode(any(String.class)))
                .willReturn(false);

        EventGroup expectedEventGroup = EventGroup.createWithJoinCode(
            command.getName(), command.getDescription(), command.getImageUrl(), command.getLeader(), "UNIQUE01"
        );
        given(eventGroupRepository.save(any(EventGroup.class)))
                .willReturn(expectedEventGroup);

        // when
        EventGroup result = eventGroupService.create(command);

        // then
        assertThat(result).isEqualTo(expectedEventGroup);
        verify(eventGroupRepository, org.mockito.Mockito.times(1)).existsByJoinCode(any(String.class));
        verify(eventGroupRepository).save(any(EventGroup.class));
    }

    @Test
    @DisplayName("그룹 생성 - existsByJoinCode 메서드 호출 확인")
    void create_shouldCallExistsByJoinCodeBeforeSaving() {
        // given
        Member leader = Member.create("test@test.com", "password", "nickname", passwordEncoder);
        EventGroupCommand.Create command = EventGroupCommand.Create.create("제주도 여행", "2박 3일 여행", "https://example.com/image.jpg", leader);

        given(eventGroupRepository.existsByJoinCode(any(String.class)))
                .willReturn(false);

        EventGroup expectedEventGroup = EventGroup.createWithJoinCode(
            command.getName(), command.getDescription(), command.getImageUrl(), command.getLeader(), "TEST1234"
        );
        given(eventGroupRepository.save(any(EventGroup.class)))
                .willReturn(expectedEventGroup);

        // when
        eventGroupService.create(command);

        // then
        // existsByJoinCode가 save 이전에 호출되었는지 확인
        org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(eventGroupRepository);
        inOrder.verify(eventGroupRepository).existsByJoinCode(any(String.class));
        inOrder.verify(eventGroupRepository).save(any(EventGroup.class));
    }

    private Member createTestMember() {
        return Member.create("test@example.com", "password", "테스터", passwordEncoder);
    }
}
