package side.eventful.interfaces.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import side.eventful.domain.file.FileMetadata;
import side.eventful.domain.file.FileStorageService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FileController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("FileController 테스트")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @DisplayName("파일 업로드 - 디렉토리 지정 없이 업로드 (기본값: GENERAL)")
    @WithMockUser
    void shouldUploadFileWithDefaultDirectory() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "pdf content".getBytes()
        );

        FileMetadata metadata = FileMetadata.builder()
                .fileName("test.pdf")
                .storedFileName("uuid-67890.pdf")
                .fileUrl("https://storage.example.com/general/uuid-67890.pdf")
                .contentType(MediaType.APPLICATION_PDF_VALUE)
                .fileSize(11L)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(fileStorageService.upload(any(), eq("general")))
                .thenReturn(metadata);

        // when & then
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.file_name").value("test.pdf"))
                .andExpect(jsonPath("$.data.content_type").value(MediaType.APPLICATION_PDF_VALUE));

        verify(fileStorageService).upload(any(), eq("general"));
    }

    @Test
    @DisplayName("파일 업로드 - directory=GROUP_IMAGES")
    @WithMockUser
    void shouldUploadGroupImage() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "group-cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "group image".getBytes()
        );

        FileMetadata metadata = FileMetadata.builder()
                .fileName("group-cover.jpg")
                .storedFileName("uuid-group-123.jpg")
                .fileUrl("https://storage.example.com/group-images/uuid-group-123.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .fileSize(11L)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(fileStorageService.upload(any(), eq("group-images")))
                .thenReturn(metadata);

        // when & then
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("directory", "GROUP_IMAGES"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.file_name").value("group-cover.jpg"))
                .andExpect(jsonPath("$.data.file_url").value("https://storage.example.com/group-images/uuid-group-123.jpg"));

        verify(fileStorageService).upload(any(), eq("group-images"));
    }

    @Test
    @DisplayName("파일 업로드 - directory=PROFILE_IMAGES")
    @WithMockUser
    void shouldUploadProfileImage() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "profile image".getBytes()
        );

        FileMetadata metadata = FileMetadata.builder()
                .fileName("profile.png")
                .storedFileName("uuid-profile-456.png")
                .fileUrl("https://storage.example.com/profile-images/uuid-profile-456.png")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .fileSize(13L)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(fileStorageService.upload(any(), eq("profile-images")))
                .thenReturn(metadata);

        // when & then
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("directory", "PROFILE_IMAGES"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.file_name").value("profile.png"))
                .andExpect(jsonPath("$.data.file_url").value("https://storage.example.com/profile-images/uuid-profile-456.png"));

        verify(fileStorageService).upload(any(), eq("profile-images"));
    }

    @Test
    @DisplayName("파일 업로드 - 허용되지 않은 디렉토리는 에러 반환")
    @WithMockUser
    void shouldReturnErrorForInvalidDirectory() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        // when & then
        // Spring이 Enum 변환 실패 시 500을 반환 (예외 핸들러 추가 전까지)
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("directory", "INVALID_DIRECTORY"))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // TODO: 글로벌 예외 핸들러 추가 후 400으로 변경
    }
}
