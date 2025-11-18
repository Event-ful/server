package side.eventful.domain.file;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 업로드된 파일의 메타데이터
 * 도메인에서 파일 저장 결과를 표현하는 불변 객체
 */
@Getter
@Builder
public class FileMetadata {
    private final String fileName;           // 원본 파일명
    private final String storedFileName;     // 저장된 파일명 (UUID 등으로 생성)
    private final String fileUrl;            // 접근 가능한 URL
    private final String contentType;        // MIME 타입
    private final Long fileSize;             // 파일 크기 (bytes)
    private final LocalDateTime uploadedAt;  // 업로드 시간

    /**
     * 파일 확장자 추출
     */
    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 파일이 이미지인지 확인
     */
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }
}

