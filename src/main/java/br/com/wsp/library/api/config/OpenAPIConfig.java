package br.com.wsp.library.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@io.swagger.v3.oas.annotations.OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API de Gerenciamento de Biblioteca",
                version = "v1.0.0",
                description = "API REST para gerenciamento de livros de uma biblioteca.",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Equipe de Desenvolvimento",
                        email = "dev@biblioteca.com",
                        url = "https://github.com/biblioteca"
                ),
                license = @io.swagger.v3.oas.annotations.info.License(
                        name = "Apache 2.0",
                        url = "http://springdoc.org"
                )
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(url = "http://localhost:8081", description = "Servidor de Desenvolvimento")
        },
        tags = {
                @io.swagger.v3.oas.annotations.tags.Tag(name = "Livros", description = "Operações relacionadas ao gerenciamento de livros")
        }
)
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
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
                                - Spring Boot 4.x
                                - MongoDB
                                - Redis
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@biblioteca.com")
                                .url("https://github.com/biblioteca"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor de Desenvolvimento")
                ))
                .tags(List.of(
                        new Tag().name("Livros").description("Operações relacionadas ao gerenciamento de livros")
                ));
    }
}
