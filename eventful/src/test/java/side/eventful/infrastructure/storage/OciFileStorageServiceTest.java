package side.eventful.infrastructure.storage;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import side.eventful.domain.file.FileMetadata;
import side.eventful.global.config.OciStorageProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OciFileStorageService 테스트")
class OciFileStorageServiceTest {

    @Mock
    private ObjectStorage objectStorage;

    @Mock
    private OciStorageProperties properties;

    @InjectMocks
    private OciFileStorageService ociFileStorageService;

    /**
     * OCI 기본 설정을 모킹하는 헬퍼 메소드
     * delete, exists 같은 메소드에서 사용 (region 불필요)
     */
    private void setupBasicOciProperties() {
        when(properties.getNamespace()).thenReturn("test-namespace");
        when(properties.getBucket()).thenReturn("test-bucket");
    }

    /**
     * OCI 전체 설정을 모킹하는 헬퍼 메소드
     * upload 같은 메소드에서 사용 (region 포함)
     */
    private void setupFullOciProperties() {
        setupBasicOciProperties();
        when(properties.getRegion()).thenReturn("ap-chuncheon-1");
    }

    @Test
    @DisplayName("MultipartFile을 성공적으로 업로드해야 한다")
    void shouldUploadMultipartFile() throws Exception {
        // given
        setupFullOciProperties(); // region이 필요한 업로드

        MultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        String directory = "group-images";

        // when
        FileMetadata result = ociFileStorageService.upload(file, directory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo("test-image.jpg");
        assertThat(result.getStoredFileName()).endsWith(".jpg");
        assertThat(result.getContentType()).isEqualTo("image/jpeg");
        assertThat(result.getFileSize()).isEqualTo(file.getSize());
        assertThat(result.getFileUrl()).contains("objectstorage");

        // OCI SDK 호출 검증
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(objectStorage).putObject(captor.capture());

        PutObjectRequest request = captor.getValue();
        assertThat(request.getNamespaceName()).isEqualTo("test-namespace");
        assertThat(request.getBucketName()).isEqualTo("test-bucket");
        assertThat(request.getObjectName()).startsWith("group-images/");
    }

    @Test
    @DisplayName("빈 파일을 업로드하면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenUploadingEmptyFile() {
        // given
        // validateFile()에서 먼저 검증하므로 properties 모킹 불필요
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // when & then
        assertThatThrownBy(() -> ociFileStorageService.upload(emptyFile, "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일이 비어있습니다");

        // OCI SDK는 호출되지 않아야 함
        verify(objectStorage, never()).putObject(any());
    }

    @Test
    @DisplayName("파일명이 없으면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenFileNameIsNull() {
        // given
        // validateFile()에서 먼저 검증하므로 properties 모킹 불필요
        MultipartFile fileWithoutName = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> ociFileStorageService.upload(fileWithoutName, "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일명이 유효하지 않습니다");

        // OCI SDK는 호출되지 않아야 함
        verify(objectStorage, never()).putObject(any());
    }

    @Test
    @DisplayName("InputStream으로 파일을 업로드할 수 있어야 한다")
    void shouldUploadFromInputStream() {
        // given
        setupFullOciProperties(); // region이 필요한 업로드

        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
        String fileName = "test.txt";
        String contentType = "text/plain";
        long size = 12L;
        String directory = "documents";

        // when
        FileMetadata result = ociFileStorageService.upload(inputStream, fileName, contentType, size, directory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo(fileName);
        assertThat(result.getContentType()).isEqualTo(contentType);
        assertThat(result.getFileSize()).isEqualTo(size);

        verify(objectStorage).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("파일을 성공적으로 삭제해야 한다")
    void shouldDeleteFile() {
        // given
        setupBasicOciProperties(); // region 불필요

        String storedFileName = "uuid-12345.jpg";
        String directory = "group-images";

        // when
        ociFileStorageService.delete(storedFileName, directory);

        // then
        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(objectStorage).deleteObject(captor.capture());

        DeleteObjectRequest request = captor.getValue();
        assertThat(request.getNamespaceName()).isEqualTo("test-namespace");
        assertThat(request.getBucketName()).isEqualTo("test-bucket");
        assertThat(request.getObjectName()).isEqualTo("group-images/uuid-12345.jpg");
    }

    @Test
    @DisplayName("파일 존재 여부를 확인할 수 있어야 한다")
    void shouldCheckFileExists() {
        // given
        setupBasicOciProperties(); // region 불필요

        String storedFileName = "uuid-12345.jpg";
        String directory = "group-images";

        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));
        when(objectStorage.getObject(any(GetObjectRequest.class))).thenReturn(mockResponse);

        // when
        boolean exists = ociFileStorageService.exists(storedFileName, directory);

        // then
        assertThat(exists).isTrue();
        verify(objectStorage).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("파일이 존재하지 않으면 false를 반환해야 한다")
    void shouldReturnFalseWhenFileDoesNotExist() {
        // given
        setupBasicOciProperties(); // region 불필요

        String storedFileName = "nonexistent.jpg";
        String directory = "group-images";

        when(objectStorage.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("File not found"));

        // when
        boolean exists = ociFileStorageService.exists(storedFileName, directory);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("디렉토리가 비어있으면 파일명만 사용해야 한다")
    void shouldUseFileNameOnlyWhenDirectoryIsEmpty() throws Exception {
        // given
        setupFullOciProperties(); // region이 필요한 업로드

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        // when
        ociFileStorageService.upload(file, "");

        // then
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(objectStorage).putObject(captor.capture());

        PutObjectRequest request = captor.getValue();
        // 디렉토리 없이 파일명만 사용
        assertThat(request.getObjectName()).doesNotContain("/");
    }
}
