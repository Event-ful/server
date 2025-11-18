package side.eventful.global.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * OCI Object Storage 설정
 * OCI SDK를 사용하여 Object Storage 클라이언트를 생성
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OciConfiguration {

    private final OciStorageProperties properties;

    /**
     * OCI Object Storage 클라이언트 Bean 생성
     * 환경에 따라 다른 인증 방식 사용:
     * - CONFIG_FILE: ~/.oci/config 파일 (로컬 개발)
     * - INSTANCE_PRINCIPAL: OCI VM의 Instance Principal (운영 환경)
     * - SIMPLE: 환경 변수 기반 인증 (운영 환경)
     */
    @Bean
    public ObjectStorage objectStorage() {
        log.info("OCI ObjectStorage 클라이언트 초기화 시작");
        log.info("인증 타입: {}", properties.getAuthType());
        log.info("Region: {}", properties.getRegion());

        try {
            AuthenticationDetailsProvider provider = createAuthenticationProvider();

            // ObjectStorage 클라이언트 생성
            ObjectStorage client = ObjectStorageClient.builder()
                    .region(properties.getRegion())
                    .build(provider);

            log.info("OCI ObjectStorage 클라이언트 초기화 완료");
            return client;

        } catch (Exception e) {
            log.error("OCI ObjectStorage 클라이언트 초기화 실패", e);
            throw new IllegalStateException("OCI ObjectStorage 클라이언트를 초기화할 수 없습니다.", e);
        }
    }

    /**
     * 설정된 인증 타입에 따라 적절한 AuthenticationDetailsProvider 생성
     */
    private AuthenticationDetailsProvider createAuthenticationProvider() throws IOException {
        return switch (properties.getAuthType()) {
            case CONFIG_FILE -> createConfigFileProvider();
            case INSTANCE_PRINCIPAL -> createInstancePrincipalProvider();
            case SIMPLE -> createSimpleProvider();
        };
    }

    /**
     * Config 파일 기반 인증 제공자 생성
     * 로컬 개발 환경에서 사용
     */
    private AuthenticationDetailsProvider createConfigFileProvider() {
        log.info("Config 파일 기반 인증 사용: {}", properties.getConfigFile());
        log.info("Profile: {}", properties.getProfile());

        try {
            ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(
                    properties.getConfigFile(),
                    properties.getProfile()
            );
            return new ConfigFileAuthenticationDetailsProvider(configFile);
        } catch (IOException e) {
            log.error("OCI 설정 파일을 읽는 중 오류 발생: {}", properties.getConfigFile(), e);
            throw new IllegalStateException(
                    String.format("OCI 설정 파일을 찾을 수 없습니다: %s. " +
                            "~/.oci/config 파일이 있는지 확인하고, 올바른 인증 정보가 설정되어 있는지 확인하세요.",
                            properties.getConfigFile()),
                    e
            );
        }
    }

    /**
     * Instance Principal 인증 제공자 생성
     * OCI VM 인스턴스에서 실행할 때 사용 (운영 환경)
     * 별도의 인증 파일이나 키가 필요 없음
     */
    private AuthenticationDetailsProvider createInstancePrincipalProvider() {
        log.info("Instance Principal 인증 사용 (OCI VM 환경)");
        return (AuthenticationDetailsProvider) InstancePrincipalsAuthenticationDetailsProvider.builder().build();
    }

    /**
     * Simple 인증 제공자 생성
     * 환경 변수로 인증 정보를 직접 주입할 때 사용
     */
    private AuthenticationDetailsProvider createSimpleProvider() {
        log.info("Simple 인증 사용 (환경 변수 기반)");

        if (properties.getTenantId() == null || properties.getUserId() == null ||
            properties.getFingerprint() == null || properties.getPrivateKey() == null) {
            throw new IllegalStateException(
                    "Simple 인증을 사용하려면 tenantId, userId, fingerprint, privateKey가 모두 설정되어야 합니다."
            );
        }

        // Private Key를 직접 문자열로 전달
        SimplePrivateKeySupplier keySupplier = new SimplePrivateKeySupplier(properties.getPrivateKey());

        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(properties.getTenantId())
                .userId(properties.getUserId())
                .fingerprint(properties.getFingerprint())
                .privateKeySupplier(keySupplier)
                .passPhrase(properties.getPassphrase())
                .region(com.oracle.bmc.Region.fromRegionCodeOrId(properties.getRegion()))
                .build();
    }
}
