package side.eventful.domain.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FileMetadata 도메인 테스트")
class FileMetadataTest {

    private FileMetadata fileMetadata;

    @BeforeEach
    void setUp() {
        fileMetadata = FileMetadata.builder()
                .fileName("test-image.jpg")
                .storedFileName("uuid-12345.jpg")
                .fileUrl("https://example.com/files/uuid-12345.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("파일 확장자를 정확히 추출해야 한다")
    void shouldExtractFileExtension() {
        // when
        String extension = fileMetadata.getFileExtension();

        // then
        assertThat(extension).isEqualTo(".jpg");
    }

    @Test
    @DisplayName("확장자가 없는 파일명은 빈 문자열을 반환해야 한다")
    void shouldReturnEmptyStringWhenNoExtension() {
        // given
        FileMetadata noExtFile = FileMetadata.builder()
                .fileName("testfile")
                .build();

        // when
        String extension = noExtFile.getFileExtension();

        // then
        assertThat(extension).isEmpty();
    }

    @Test
    @DisplayName("이미지 파일 여부를 정확히 판단해야 한다")
    void shouldIdentifyImageFile() {
        // when & then
        assertThat(fileMetadata.isImage()).isTrue();
    }

    @Test
    @DisplayName("이미지가 아닌 파일은 false를 반환해야 한다")
    void shouldReturnFalseForNonImageFile() {
        // given
        FileMetadata pdfFile = FileMetadata.builder()
                .fileName("document.pdf")
                .contentType("application/pdf")
                .build();

        // when & then
        assertThat(pdfFile.isImage()).isFalse();
    }

    @Test
    @DisplayName("contentType이 null인 경우 이미지 판단 시 false를 반환해야 한다")
    void shouldReturnFalseWhenContentTypeIsNull() {
        // given
        FileMetadata nullContentType = FileMetadata.builder()
                .fileName("test.jpg")
                .contentType(null)
                .build();

        // when & then
        assertThat(nullContentType.isImage()).isFalse();
    }
}

