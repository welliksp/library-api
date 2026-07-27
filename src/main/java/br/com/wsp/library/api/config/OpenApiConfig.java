package br.com.wsp.library.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {


    public OpenAPI customOpenApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Biblioteca")
                        .description("""
                                API REST para gerenciamento de livros de uma biblioteca.
                                
                                **Funcionalidades:**
                                - CRUD completo de livros
                                - Cache Redis para otimização de leitura
                                - Paginação e filtros
                                - Validações de negócio
                                
                                **Tecnologias:**
                                - Java 21
                                - Spring Boot 4.1.0
                                - MongoDB
                                - Redis
                                
                                **Documentação adicional:**
                                - [Swagger UI](http://localhost:8080/swagger-ui.html)
                                - [OpenAPI JSON](http://localhost:8080/v3/api-docs)
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@biblioteca.com")
                                .url("https://github.com/biblioteca"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.biblioteca.com")
                                .description("Servidor de Produção")
                ))
                .tags(Arrays.asList(
                ));

    }
}

