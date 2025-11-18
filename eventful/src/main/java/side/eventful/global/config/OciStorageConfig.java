package side.eventful.global.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 인스턴스 프린시펄 인증 제공자 (클라우드 환경)
     */
    @Bean
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
