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
     * 로컬 개발 환경에서 사용할 config 파일 경로
     * 기본값: ~/.oci/config
     */
    private String configFile = System.getProperty("user.home") + "/.oci/config";

    /**
     * config 파일에서 사용할 프로파일
     * 기본값: DEFAULT
     */
    private String profile = "DEFAULT";
}

