package side.eventful.global.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * OCI Object Storage 클라이언트 설정
 * 환경에 따라 다른 인증 방식을 사용
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OciStorageConfig {

    private final OciStorageProperties properties;

    /**
     * 로컬 개발 환경: ~/.oci/config 파일을 사용한 인증
     */
    @Bean
    @Profile("!prod")
    public ConfigFileAuthenticationDetailsProvider localAuthenticationDetailsProvider() throws IOException {
        log.info("OCI 로컬 인증 초기화 - config 파일: {}, 프로파일: {}",
                properties.getConfigFile(), properties.getProfile());

        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(
                properties.getConfigFile(),
                properties.getProfile()
        );

        return new ConfigFileAuthenticationDetailsProvider(configFile);
    }

    /**
     * 프로덕션 환경 (OCI VM): Instance Principals 인증
     * VM에 부여된 Dynamic Group 권한으로 인증
     */
    @Bean
    @Profile("prod")
    public InstancePrincipalsAuthenticationDetailsProvider instancePrincipalsAuthenticationDetailsProvider() {
        log.info("OCI Instance Principals 인증 초기화");

        try {
            InstancePrincipalsAuthenticationDetailsProvider provider =
                    InstancePrincipalsAuthenticationDetailsProvider.builder().build();

            log.info("✅ Instance Principals 인증 프로바이더 생성 성공");
            log.info("Region: {}", provider.getRegion());

            return provider;

        } catch (Exception e) {
            log.error("❌ Instance Principals 인증 초기화 실패", e);
            log.error("");
            log.error("📋 문제 해결 가이드:");
            log.error("1. 이 애플리케이션이 OCI VM 인스턴스에서 실행되고 있는지 확인");
            log.error("2. VM이 Dynamic Group에 포함되어 있는지 확인");
            log.error("3. OCI 메타데이터 서비스에 접근 가능한지 확인");
            log.error("   (방화벽이나 보안 그룹이 169.254.169.254 접근을 차단하지 않는지)");
            log.error("");
            throw new IllegalStateException("Instance Principals 인증 초기화 실패. OCI VM 환경 설정을 확인해주세요.", e);
        }
    }

    /**
     * Object Storage 클라이언트 생성 (로컬 환경)
     */
    @Bean
    @Profile("!prod")
    public ObjectStorage objectStorageClientLocal(ConfigFileAuthenticationDetailsProvider authProvider) {
        log.info("OCI Object Storage 클라이언트 생성 (로컬) - region: {}, namespace: {}, bucket: {}",
                properties.getRegion(), properties.getNamespace(), properties.getBucket());

        ObjectStorage client = ObjectStorageClient.builder()
                .build(authProvider);

        client.setRegion(properties.getRegion());

        return client;
    }

    /**
     * Object Storage 클라이언트 생성 (프로덕션 환경)
     */
    @Bean
    @Profile("prod")
    public ObjectStorage objectStorageClientProd(InstancePrincipalsAuthenticationDetailsProvider authProvider) {
        log.info("OCI Object Storage 클라이언트 생성 (프로덕션) - region: {}, namespace: {}, bucket: {}",
                properties.getRegion(), properties.getNamespace(), properties.getBucket());

        ObjectStorage client = ObjectStorageClient.builder()
                .build(authProvider);

        client.setRegion(properties.getRegion());

        return client;
    }
}
