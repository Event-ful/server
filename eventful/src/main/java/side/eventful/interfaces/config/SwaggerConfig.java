package side.eventful.interfaces.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("Eventful API Document")
            .version("v1.0.0")
            .description("Eventful API 명세서입니다.");

        return new OpenAPI()
            .info(info)
            .components(new Components());
    }

}
