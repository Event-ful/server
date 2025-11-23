package side.eventful.global.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
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
    public AuthenticationDetailsProvider localAuthenticationDetailsProvider() throws IOException {
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
        return InstancePrincipalsAuthenticationDetailsProvider.builder().build();
    }

    /**
     * Object Storage 클라이언트 생성
     */
    @Bean
    public ObjectStorage objectStorageClient(AuthenticationDetailsProvider authProvider) {
        log.info("OCI Object Storage 클라이언트 생성 - region: {}, namespace: {}, bucket: {}",
                properties.getRegion(), properties.getNamespace(), properties.getBucket());

        ObjectStorage client = ObjectStorageClient.builder()
                .build(authProvider);

        client.setRegion(properties.getRegion());

        return client;
    }
}
