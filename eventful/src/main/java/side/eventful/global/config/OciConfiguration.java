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
     * ~/.oci/config 파일에서 인증 정보를 읽어옴
     */
    @Bean
    public ObjectStorage objectStorage() throws IOException {
        log.info("OCI ObjectStorage 클라이언트 초기화 시작");
        log.info("Config 파일 경로: {}", properties.getConfigFile());
        log.info("Profile: {}", properties.getProfile());

        try {
            // OCI config 파일 읽기
            ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(
                    properties.getConfigFile(),
                    properties.getProfile()
            );

            // 인증 정보 제공자 생성
            AuthenticationDetailsProvider provider =
                    new ConfigFileAuthenticationDetailsProvider(configFile);

            // ObjectStorage 클라이언트 생성
            ObjectStorage client = ObjectStorageClient.builder()
                    .region(properties.getRegion())
                    .build(provider);

            log.info("OCI ObjectStorage 클라이언트 초기화 완료 - Region: {}", properties.getRegion());
            return client;

        } catch (IOException e) {
            log.error("OCI 설정 파일을 읽는 중 오류 발생: {}", properties.getConfigFile(), e);
            throw new IllegalStateException(
                    String.format("OCI 설정 파일을 찾을 수 없습니다: %s. " +
                            "~/.oci/config 파일이 있는지 확인하고, 올바른 인증 정보가 설정되어 있는지 확인하세요.",
                            properties.getConfigFile()),
                    e
            );
        } catch (Exception e) {
            log.error("OCI ObjectStorage 클라이언트 초기화 실패", e);
            throw new IllegalStateException("OCI ObjectStorage 클라이언트를 초기화할 수 없습니다.", e);
        }
    }
}

