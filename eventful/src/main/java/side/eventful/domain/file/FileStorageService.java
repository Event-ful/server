package side.eventful.domain.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 파일 저장소 인터페이스
 * 실제 저장소 구현체(OCI, S3, Local 등)와의 결합도를 낮추기 위한 추상화
 */
public interface FileStorageService {

    /**
     * 파일을 저장하고 메타데이터를 반환
     *
     * @param file 업로드할 파일
     * @param directory 저장할 디렉토리 (예: "group-images", "profile-images")
     * @return 저장된 파일의 메타데이터
     */
    FileMetadata upload(MultipartFile file, String directory);

    /**
     * InputStream으로 파일 저장 (테스트나 특수한 경우)
     */
    FileMetadata upload(InputStream inputStream, String fileName, String contentType, long size, String directory);

    /**
     * 파일 삭제
     *
     * @param storedFileName 저장된 파일명
     * @param directory 저장된 디렉토리
     */
    void delete(String storedFileName, String directory);

    /**
     * 파일 존재 여부 확인
     */
    boolean exists(String storedFileName, String directory);

    /**
     * 파일 URL 조회 (Public URL 또는 Pre-signed URL)
     */
    String getFileUrl(String storedFileName, String directory);
}

