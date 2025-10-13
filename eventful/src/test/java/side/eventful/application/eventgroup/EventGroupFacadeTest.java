package side.eventful.application.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.eventgroup.EventGroup;
import side.eventful.domain.eventgroup.EventGroupCommand;
import side.eventful.domain.eventgroup.EventGroupService;
import side.eventful.domain.member.Member;
import side.eventful.domain.member.auth.AuthService;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventGroupFacadeTest {

    @Mock
    private EventGroupService eventGroupService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private EventGroupFacade eventGroupFacade;

    private PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @Test
    @DisplayName("그룹 생성 - 정상 케이스")
    void create_withValidInput_success() {
        // given
        String name = "제주도 여행";
        String description = "2박 3일 제주도 여행 그룹";
        String imageUrl = "https://example.com/image.jpg";
        EventGroupCriteria.Create criteria = EventGroupCriteria.Create.create(name, description, imageUrl);

        Member authenticatedMember = createTestMember();
        EventGroup createdEventGroup = EventGroup.create(name, description, imageUrl, authenticatedMember);

        given(authService.getAuthenticatedMember())
                .willReturn(authenticatedMember);
        given(eventGroupService.create(any(EventGroupCommand.Create.class)))
                .willReturn(createdEventGroup);

        // when
        EventGroupResult.Create result = eventGroupFacade.create(criteria);

        // then
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getImageUrl()).isEqualTo(imageUrl);
        assertThat(result.getJoinPassword()).isNotNull();
        assertThat(result.getId()).isEqualTo(createdEventGroup.getId());
        verify(eventGroupService).create(any(EventGroupCommand.Create.class));
    }

    @Test
    @DisplayName("그룹 생성 - 인증되지 않은 사용자")
    void create_withUnauthenticatedUser_throwsException() {
        // given
        EventGroupCriteria.Create criteria = EventGroupCriteria.Create.create("제주도 여행", "설명", "https://example.com/image.jpg");

        given(authService.getAuthenticatedMember())
                .willThrow(new IllegalStateException("인증되지 않은 사용자입니다"));

        // when, then
        assertThrows(IllegalStateException.class, () -> eventGroupFacade.create(criteria));
    }

    @Test
    @DisplayName("그룹 생성 - 빈 이름으로 생성 시도")
    void create_withEmptyName_throwsException() {
        // given
        EventGroupCriteria.Create criteria = EventGroupCriteria.Create.create("", "설명", "https://example.com/image.jpg");

        Member authenticatedMember = createTestMember();
        given(authService.getAuthenticatedMember())
                .willReturn(authenticatedMember);

        given(eventGroupService.create(any(EventGroupCommand.Create.class)))
                .willThrow(new IllegalArgumentException("그룹 이름은 필수입니다"));

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupFacade.create(criteria));
    }

    @Test
    @DisplayName("그룹 생성 - null 이름으로 생성 시도")
    void create_withNullName_throwsException() {
        // given
        EventGroupCriteria.Create criteria = EventGroupCriteria.Create.create(null, "설명", "https://example.com/image.jpg");

        Member authenticatedMember = createTestMember();
        given(authService.getAuthenticatedMember())
                .willReturn(authenticatedMember);

        given(eventGroupService.create(any(EventGroupCommand.Create.class)))
                .willThrow(new IllegalArgumentException("그룹 이름은 필수입니다"));

        // when, then
        assertThrows(IllegalArgumentException.class, () -> eventGroupFacade.create(criteria));
    }

    @Test
    @DisplayName("그룹 생성 - 서비스 계층 에러 전파")
    void create_withServiceError_throwsException() {
        // given
        EventGroupCriteria.Create criteria = EventGroupCriteria.Create.create("제주도 여행", "설명", "https://example.com/image.jpg");

        Member authenticatedMember = createTestMember();
        given(authService.getAuthenticatedMember())
                .willReturn(authenticatedMember);

        given(eventGroupService.create(any(EventGroupCommand.Create.class)))
                .willThrow(new RuntimeException("데이터베이스 오류"));

        // when, then
        assertThrows(RuntimeException.class, () -> eventGroupFacade.create(criteria));
    }

    private Member createTestMember() {
        return Member.create("test@example.com", "password", "테스터", passwordEncoder);
    }
}
