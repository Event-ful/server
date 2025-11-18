package side.eventful.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OCI Object Storage 설정 프로퍼티
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oci")
public class OciStorageProperties {
    private String namespace;
    private String bucket;
    private String region;

    /**
     * OCI 인증 타입
     * - CONFIG_FILE: ~/.oci/config 파일 사용 (로컬 개발)
     * - INSTANCE_PRINCIPAL: OCI VM의 Instance Principal 사용 (운영 환경)
     * - SIMPLE: 환경 변수로 직접 인증 정보 주입
     */
    private AuthType authType = AuthType.CONFIG_FILE;

    /**
     * 로컬 개발 환경에서 사용할 config 파일 경로
     * 기본값: ~/.oci/config
     * authType이 CONFIG_FILE일 때만 사용
     */
    private String configFile = System.getProperty("user.home") + "/.oci/config";

    /**
     * config 파일에서 사용할 프로파일
     * 기본값: DEFAULT
     */
    private String profile = "DEFAULT";

    /**
     * Simple 인증 방식에서 사용할 필드들
     * authType이 SIMPLE일 때만 사용
     */
    private String tenantId;
    private String userId;
    private String fingerprint;
    private String privateKey;
    private String passphrase;

    public enum AuthType {
        CONFIG_FILE,        // 파일 기반 인증 (로컬)
        INSTANCE_PRINCIPAL, // Instance Principal 인증 (VM)
        SIMPLE             // 환경 변수 기반 인증
    }
}
