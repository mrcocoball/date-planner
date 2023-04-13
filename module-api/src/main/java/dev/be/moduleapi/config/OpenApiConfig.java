package dev.be.moduleapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI config() {

        String jwtSchemeName = "jwtAuth";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("Authorization"));

        return new OpenAPI()
                .info(new Info().title("Date-planner API 서버")
                        .description("API 명세서와 테스트를 위한 API를 제공하고 있습니다")
                        .version("v1")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("더블코코볼 mrcocoball")
                                .email("gunmaru93@gmai.com")
                                .url("https://github.com/mrcocoball/date-planner")))
                .addSecurityItem(securityRequirement)
                .components(components);
    }


    /*

    // 그룹 설정된 API들만 문서에 노출시킬 수 있게 한다
    @Bean
    public GroupedOpenApi sampleGroupOpenApi() {
        String[] paths = {"/diary/**"};

        return GroupedOpenApi.builder().group("샘플 API").pathsToMatch(paths).build();
    }

     */
}
