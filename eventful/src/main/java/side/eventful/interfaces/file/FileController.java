package side.eventful.interfaces.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import side.eventful.domain.file.FileDirectory;
import side.eventful.domain.file.FileMetadata;
import side.eventful.domain.file.FileStorageService;

/**
 * 파일 업로드 API 컨트롤러
 * 이미지 및 기타 파일 업로드를 처리
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
@Tag(name = "File", description = "파일 업로드 API")
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * 파일 업로드
     *
     * @param file 업로드할 파일 (MultipartFile)
     * @param directory 저장할 디렉토리 (선택, 기본값: GENERAL)
     *                  허용된 값: GENERAL, GROUP_IMAGES, PROFILE_IMAGES
     * @return 업로드된 파일 정보 (URL, 메타데이터 등)
     */
    @Operation(
        summary = "파일 업로드",
        description = "파일을 업로드하고 저장된 파일 정보를 반환합니다. " +
                     "directory 파라미터로 저장 위치를 지정할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 없음, 빈 파일 등)"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<side.eventful.global.response.ApiResponse<FileResponse.Upload>> uploadFile(
            @Parameter(
                description = "업로드할 파일",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("file") MultipartFile file,

            @Parameter(
                description = "파일 저장 디렉토리",
                schema = @Schema(
                    implementation = FileDirectory.class,
                    defaultValue = "GENERAL",
                    allowableValues = {"GENERAL", "GROUP_IMAGES", "PROFILE_IMAGES"}
                )
            )
            @RequestParam(value = "directory", required = false, defaultValue = "GENERAL") FileDirectory directory) {

        log.info("파일 업로드 요청 - 파일명: {}, 크기: {}, 디렉토리: {}",
                file.getOriginalFilename(), file.getSize(), directory.getPath());

        // 파일 저장
        FileMetadata metadata = fileStorageService.upload(file, directory.getPath());

        log.info("파일 업로드 성공 - URL: {}", metadata.getFileUrl());

        // 응답 변환
        FileResponse.Upload response = FileResponse.Upload.from(metadata);

        return ResponseEntity.ok(side.eventful.global.response.ApiResponse.ok(response));
    }
}
