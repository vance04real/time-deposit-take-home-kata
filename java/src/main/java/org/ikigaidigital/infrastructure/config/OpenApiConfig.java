package org.ikigaidigital.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "XA Bank Time Deposit API",
                version = "1.0.0",
                description = "RESTful API for managing time deposits including balance updates and retrieval",
                contact = @Contact(
                        name = "XA Bank Development Team",
                        email = "dev@xabank.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080/api", description = "Local development server"),
                @Server(url = "https://api.xabank.com/api", description = "Production server")
        }
)
public class OpenApiConfig {
}