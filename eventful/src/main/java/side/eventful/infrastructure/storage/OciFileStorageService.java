package side.eventful.infrastructure.storage;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import side.eventful.domain.file.FileMetadata;
import side.eventful.domain.file.FileStorageService;
import side.eventful.global.config.OciStorageProperties;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OCI Object Storage 구현체
 * 실제 OCI SDK를 사용해서 파일을 저장하고 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OciFileStorageService implements FileStorageService {

    private final ObjectStorage objectStorage;
    private final OciStorageProperties properties;

    @Override
    public FileMetadata upload(MultipartFile file, String directory) {
        validateFile(file);

        // TODO: 향후 이미지 리사이징 요구사항이 들어오면 여기서 분기 처리
        // 예시 확장 방법:
        // if (directory.equals("group-images") && isImageFile(file)) {
        //     return uploadWithResize(file, directory);
        // }
        //
        // 고려사항:
        // 1. ImageProcessingService를 별도 도메인 서비스로 분리
        // 2. 리사이징 전략 (썸네일 150x150, 중간 500x500, 원본 유지 등)
        // 3. 비동기 처리 여부 (응답 시간 vs 즉시 사용 가능 여부)
        // 4. 라이브러리 선택 (Thumbnailator, imgscalr, ImageIO 등)

        try {
            String storedFileName = generateStoredFileName(file.getOriginalFilename());
            String objectName = buildObjectName(directory, storedFileName);

            log.info("파일 업로드 시작 - 원본: {}, 저장명: {}, 경로: {}",
                file.getOriginalFilename(), storedFileName, objectName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .namespaceName(properties.getNamespace())
                .bucketName(properties.getBucket())
                .objectName(objectName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .putObjectBody(file.getInputStream())
                .build();

            objectStorage.putObject(putObjectRequest);

            String fileUrl = buildFileUrl(objectName);

            log.info("파일 업로드 완료 - URL: {}", fileUrl);

            return FileMetadata.builder()
                .fileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileUrl(fileUrl)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        } catch (IOException e) {
            log.error("파일 업로드 실패 - 파일명: {}", file.getOriginalFilename(), e);
            throw new FileUploadException("파일 업로드에 실패했습니다.", e);
        }
    }

    @Override
    public FileMetadata upload(InputStream inputStream, String fileName, String contentType, long size, String directory) {
        String storedFileName = generateStoredFileName(fileName);
        String objectName = buildObjectName(directory, storedFileName);

        log.info("InputStream 파일 업로드 시작 - 원본: {}, 저장명: {}", fileName, storedFileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .namespaceName(properties.getNamespace())
            .bucketName(properties.getBucket())
            .objectName(objectName)
            .contentType(contentType)
            .contentLength(size)
            .putObjectBody(inputStream)
            .build();

        objectStorage.putObject(putObjectRequest);

        String fileUrl = buildFileUrl(objectName);

        return FileMetadata.builder()
            .fileName(fileName)
            .storedFileName(storedFileName)
            .fileUrl(fileUrl)
            .contentType(contentType)
            .fileSize(size)
            .uploadedAt(LocalDateTime.now())
            .build();
    }

    @Override
    public void delete(String storedFileName, String directory) {
        String objectName = buildObjectName(directory, storedFileName);

        log.info("파일 삭제 시작 - 경로: {}", objectName);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .namespaceName(properties.getNamespace())
            .bucketName(properties.getBucket())
            .objectName(objectName)
            .build();

        objectStorage.deleteObject(deleteObjectRequest);

        log.info("파일 삭제 완료 - 경로: {}", objectName);
    }

    @Override
    public boolean exists(String storedFileName, String directory) {
        String objectName = buildObjectName(directory, storedFileName);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .namespaceName(properties.getNamespace())
                .bucketName(properties.getBucket())
                .objectName(objectName)
                .build();

            GetObjectResponse response = objectStorage.getObject(getObjectRequest);
            return response.getInputStream() != null;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getFileUrl(String storedFileName, String directory) {
        String objectName = buildObjectName(directory, storedFileName);
        return buildFileUrl(objectName);
    }

    /**
     * 파일 유효성 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }
    }

    /**
     * 저장할 파일명 생성 (UUID + 확장자)
     */
    private String generateStoredFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Object Storage 내부 경로 생성 (디렉토리/파일명)
     */
    private String buildObjectName(String directory, String fileName) {
        if (directory == null || directory.isBlank()) {
            return fileName;
        }
        // 디렉토리 끝에 / 가 있으면 제거
        directory = directory.endsWith("/") ? directory.substring(0, directory.length() - 1) : directory;
        return directory + "/" + fileName;
    }

    /**
     * Public URL 생성
     * 실제 프로덕션에서는 Pre-signed URL을 사용하거나, CDN을 통해 접근하는 것을 권장
     */
    private String buildFileUrl(String objectName) {
        return String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
            properties.getRegion(),
            properties.getNamespace(),
            properties.getBucket(),
            objectName);
    }
}
