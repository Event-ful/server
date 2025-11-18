package side.eventful.interfaces.file;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {
    private String fileName;
    private String storedFileName;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private String uploadedAt;
}

