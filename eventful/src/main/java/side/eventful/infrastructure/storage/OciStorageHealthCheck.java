package side.eventful.infrastructure.storage;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.HeadBucketRequest;
import com.oracle.bmc.objectstorage.responses.HeadBucketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import side.eventful.global.config.OciStorageProperties;

/**
 * OCI Object Storage 연결 및 권한 검증
 *
 * 애플리케이션 시작 시 버킷 접근 가능 여부를 체크하여
 * 설정 오류를 조기에 발견할 수 있도록 함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OciStorageHealthCheck {

    private final ObjectStorage objectStorage;
    private final OciStorageProperties properties;

    /**
     * 애플리케이션 준비 완료 시점에 버킷 접근 가능 여부 검증
     *
     * 실패 시 경고 로그를 남기고, 상세한 문제 해결 가이드 제공
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkStorageAccessibility() {
        log.info("=".repeat(80));
        log.info("OCI Object Storage 연결 상태 확인 시작");
        log.info("Namespace: {}", properties.getNamespace());
        log.info("Bucket: {}", properties.getBucket());
        log.info("Region: {}", properties.getRegion());
        log.info("Auth Type: {}", properties.getAuthType());

        try {
            HeadBucketRequest request = HeadBucketRequest.builder()
                .namespaceName(properties.getNamespace())
                .bucketName(properties.getBucket())
                .build();

            HeadBucketResponse response = objectStorage.headBucket(request);

            log.info("✅ OCI Object Storage 버킷 접근 성공!");
            log.info("Bucket ETag: {}", response.getETag());
            log.info("=".repeat(80));

        } catch (com.oracle.bmc.model.BmcException e) {
            log.error("=".repeat(80));
            log.error("❌ OCI Object Storage 버킷 접근 실패!");
            log.error("Error Code: {}", e.getStatusCode());
            log.error("Error Message: {}", e.getMessage());
            log.error("");

            if (e.getStatusCode() == 404) {
                log.error("📋 문제 해결 가이드 (404 BucketNotFound):");
                log.error("");
                log.error("1. OCI 콘솔에서 버킷이 생성되어 있는지 확인");
                log.error("   → https://cloud.oracle.com/object-storage/buckets");
                log.error("   → Namespace: {}", properties.getNamespace());
                log.error("   → Bucket 이름: {}", properties.getBucket());
                log.error("   → Region: {}", properties.getRegion());
                log.error("");
                log.error("2. Instance Principal 권한 확인 (프로덕션 환경인 경우)");
                log.error("   2-1. Dynamic Group이 생성되어 있고, 현재 VM이 포함되어 있는지 확인");
                log.error("        규칙 예시: instance.compartment.id = 'ocid1.compartment...'");
                log.error("");
                log.error("   2-2. Policy에 Object Storage 접근 권한이 부여되어 있는지 확인");
                log.error("        필요한 Policy 예시:");
                log.error("        Allow dynamic-group <그룹명> to manage objects in compartment <컴파트먼트명>");
                log.error("        Allow dynamic-group <그룹명> to read buckets in compartment <컴파트먼트명>");
                log.error("");
                log.error("3. 버킷이 다른 Compartment에 있는 경우");
                log.error("   → Policy의 compartment 범위 확인 필요");
                log.error("");
            } else if (e.getStatusCode() == 401 || e.getStatusCode() == 403) {
                log.error("📋 문제 해결 가이드 (인증/권한 오류):");
                log.error("");
                log.error("1. Instance Principal 설정 확인");
                log.error("   → VM이 Dynamic Group에 포함되어 있는지 확인");
                log.error("");
                log.error("2. Policy 권한 확인");
                log.error("   → Object Storage에 대한 적절한 권한이 부여되어 있는지 확인");
                log.error("");
            }

            log.error("=".repeat(80));
            log.error("");
            log.error("⚠️  파일 업로드 기능이 정상 작동하지 않을 수 있습니다.");
            log.error("    위 가이드를 참고하여 OCI 설정을 확인해주세요.");
            log.error("");

        } catch (Exception e) {
            log.error("❌ 예상치 못한 오류 발생", e);
        }
    }
}
