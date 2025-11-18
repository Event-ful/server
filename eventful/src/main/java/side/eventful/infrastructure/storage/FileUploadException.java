package side.eventful.infrastructure.storage;

/**
 * 파일 업로드 실패 예외
 */
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
