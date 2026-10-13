package com.bankcore.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración centralizada de Documentación OpenAPI 3.0 / Swagger UI.
 * Expone la especificación viva del contrato bancario en /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BankCore Digital - Core Banking API")
                        .version("1.0.0")
                        .description("API RESTful transaccional de misión crítica construida con Spring Boot 3.3.4 (Java 21 LTS), "
                                + "Arquitectura Hexagonal (Ports & Adapters), semántica HTTP estricta y RFC 7807.")
                        .contact(new Contact()
                                .name("Centro de Excelencia de Arquitectura (CoE)")
                                .email("arquitectura@bankcore.digital")
                                .url("https://bankcore.digital"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Entorno Local de Desarrollo"),
                        new Server().url("https://api.bankcore.digital").description("Entorno Cloud Azure / Producción")
                ))
                .tags(List.of(
                        new Tag().name("Cuentas").description("Gestión del ciclo de vida de cuentas de ahorro y corrientes"),
                        new Tag().name("Transferencias").description("Motor transaccional atómico (ACID) con soporte de Idempotencia"),
                        new Tag().name("Tarjetas").description("Consulta de productos y bloqueo preventivo de seguridad")
                ));
    }
}
