package side.eventful.domain.eventgroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import side.eventful.domain.member.Member;
import side.eventful.infrastructure.security.config.TestPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventGroupTest {

    private PasswordEncoder passwordEncoder = new TestPasswordEncoder();

    @Test
    @DisplayName("이벤트 그룹을 생성할 수 있다")
    void create_validInput_success() {
        // given
        String name = "소모임";
        String description = "소모임 그룹입니다";
        String imageUrl = "https://example.com/image.jpg";
        Member leader = createTestMember();

        // when
        EventGroup eventGroup = EventGroup.create(name, description, imageUrl, leader);

        // then
        assertThat(eventGroup.getName()).isEqualTo(name);
        assertThat(eventGroup.getDescription()).isEqualTo(description);
        assertThat(eventGroup.getImageUrl()).isEqualTo(imageUrl);
        assertThat(eventGroup.getLeader()).isEqualTo(leader);
        assertThat(eventGroup.getJoinPassword()).isNotNull();
        assertThat(eventGroup.getJoinPassword()).hasSize(8); // 8자
    }

    @Test
    @DisplayName("생성된 비밀번호는 8자이고 소문자, 대문자, 특수문자를 포함한다")
    void create_validInput_generatesPasswordWithRequiredRules() {
        // given
        String name = "소모임";
        String description = "설명";
        String imageUrl = "https://example.com/image.jpg";
        Member leader = createTestMember();

        // when
        EventGroup eventGroup = EventGroup.create(name, description, imageUrl, leader);

        // then
        String password = eventGroup.getJoinPassword();
        assertThat(password).hasSize(8);
        assertThat(password).matches(".*[a-z].*"); // 소문자 포함
        assertThat(password).matches(".*[A-Z].*"); // 대문자 포함
        assertThat(password).matches(".*[!@#$%^&*()].*"); // 특수문자 포함
    }

    @Test
    @DisplayName("그룹 이름은 15자를 초과할 수 없다")
    void create_nameExceeds15Characters_throwsException() {
        // given
        String name = "매우긴그룹이름입니다열다섯자넘어요"; // 16자
        String description = "설명";
        String imageUrl = "https://example.com/image.jpg";
        Member leader = createTestMember();

        // when & then
        assertThatThrownBy(() -> EventGroup.create(name, description, imageUrl, leader))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹 이름은 15자를 초과할 수 없습니다");
    }

    @Test
    @DisplayName("그룹 소개는 200자를 초과할 수 없다")
    void create_descriptionExceeds200Characters_throwsException() {
        // given
        String name = "소모임";
        String description = "가".repeat(201); // 201자
        String imageUrl = "https://example.com/image.jpg";
        Member leader = createTestMember();

        // when & then
        assertThatThrownBy(() -> EventGroup.create(name, description, imageUrl, leader))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹 소개는 200자를 초과할 수 없습니다");
    }

    @Test
    @DisplayName("그룹 이름이 null이면 그룹을 생성할 수 없다")
    void create_nullName_throwsException() {
        // given
        String name = null;
        String description = "설명";
        String imageUrl = "https://example.com/image.jpg";
        Member leader = createTestMember();

        // when & then
        assertThatThrownBy(() -> EventGroup.create(name, description, imageUrl, leader))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹 이름은 필수입니다");
    }

    @Test
    @DisplayName("그룹장이 null이면 그룹을 생성할 수 없다")
    void create_nullLeader_throwsException() {
        // given
        String name = "소모임";
        String description = "설명";
        String imageUrl = "https://example.com/image.jpg";
        Member leader = null;

        // when & then
        assertThatThrownBy(() -> EventGroup.create(name, description, imageUrl, leader))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹장은 필수입니다");
    }

    private Member createTestMember() {
        return Member.create("test@example.com", "password", "테스터", passwordEncoder);
    }
}
