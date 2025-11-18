package side.eventful.interfaces.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 파일 업로드 응답 DTO
 */
public class FileResponse {

    @Getter
    @AllArgsConstructor
    public static class Upload {
        private String fileName;           // 원본 파일명
        private String storedFileName;     // 저장된 파일명
        private String fileUrl;            // 접근 가능한 URL
        private String contentType;        // MIME 타입
        private Long fileSize;             // 파일 크기 (bytes)
        private LocalDateTime uploadedAt;  // 업로드 시간

        public static Upload from(side.eventful.domain.file.FileMetadata metadata) {
            return new Upload(
                metadata.getFileName(),
                metadata.getStoredFileName(),
                metadata.getFileUrl(),
                metadata.getContentType(),
                metadata.getFileSize(),
                metadata.getUploadedAt()
            );
        }
    }
}

