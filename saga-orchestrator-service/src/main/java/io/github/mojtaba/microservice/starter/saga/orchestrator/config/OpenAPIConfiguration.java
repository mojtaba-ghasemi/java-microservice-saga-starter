package io.github.mojtaba.microservice.starter.saga.orchestrator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration { // SwaggerConfig

    @Value("${api.server.url:http://localhost:8080}")
   // http://localhost:8080/swagger-ui/index.html
    private String serverUrl;

    @Bean
    public OpenAPI defineOpenApi() {
        // Server configuration
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Saga Orchestrator Service API Server");

        // Contact information
        Contact contact = new Contact()
                .name("Your Name")
                .email("your.email@example.com")
                .url("https://github.com/...");

        // License information
        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        // API information
        Info info = new Info()
                .title("Saga Orchestrator Service API")
                .version("1.0.0")
                .description("This API provides endpoints for managing distributed transactions using the Saga pattern.")
                .contact(contact)
                .license(license)
                .termsOfService("http://swagger.io/terms/");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}