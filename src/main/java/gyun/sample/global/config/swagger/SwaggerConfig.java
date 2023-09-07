package gyun.sample.global.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Swagger 설정
@OpenAPIDefinition(info = @Info(title = "SAMPLE API"))
@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        paramName = "Authorization")
public class SwaggerConfig {

    @Profile(value = "dev")
    @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"/api/**"};
        return GroupedOpenApi.builder()
                .group("api v1")
                .pathsToMatch(paths)
                .build();
    }
}