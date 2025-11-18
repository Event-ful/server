package side.eventful.domain.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileDirectory Enum 테스트
 */
@DisplayName("FileDirectory 테스트")
class FileDirectoryTest {

    @Test
    @DisplayName("GENERAL은 general 경로를 가짐")
    void shouldHaveCorrectPath_General() {
        // when & then
        assertThat(FileDirectory.GENERAL.getPath()).isEqualTo("general");
        assertThat(FileDirectory.GENERAL.getDescription()).isEqualTo("일반 파일");
    }

    @Test
    @DisplayName("GROUP_IMAGES는 group-images 경로를 가짐")
    void shouldHaveCorrectPath_GroupImages() {
        // when & then
        assertThat(FileDirectory.GROUP_IMAGES.getPath()).isEqualTo("group-images");
        assertThat(FileDirectory.GROUP_IMAGES.getDescription()).isEqualTo("그룹 이미지");
    }

    @Test
    @DisplayName("PROFILE_IMAGES는 profile-images 경로를 가짐")
    void shouldHaveCorrectPath_ProfileImages() {
        // when & then
        assertThat(FileDirectory.PROFILE_IMAGES.getPath()).isEqualTo("profile-images");
        assertThat(FileDirectory.PROFILE_IMAGES.getDescription()).isEqualTo("프로필 이미지");
    }

    @Test
    @DisplayName("모든 Enum 값 확인")
    void shouldHaveExactlyThreeValues() {
        // when
        FileDirectory[] values = FileDirectory.values();

        // then
        assertThat(values).hasSize(3);
        assertThat(values).containsExactly(
                FileDirectory.GENERAL,
                FileDirectory.GROUP_IMAGES,
                FileDirectory.PROFILE_IMAGES
        );
    }
}
