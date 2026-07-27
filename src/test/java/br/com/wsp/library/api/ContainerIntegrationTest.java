package br.com.wsp.library.api;

import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.enums.Genero;
import br.com.wsp.library.api.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ContainerIntegrationTest {

    static final MongoDBContainer mongo =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0.6"));

    static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                    .withExposedPorts(6379);

    static {
        mongo.start();
        redis.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.mongodb.database", () -> "library_db_test");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.redis.password", () -> "");
    }

    @LocalServerPort int port;
    @Autowired LivroRepository livroRepository;
    @Autowired CacheManager cacheManager;

    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }
        });
        livroRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private LivroRequestDTO requestValido(String isbn) {
        return new LivroRequestDTO("Clean Code", "Robert C. Martin", isbn, 2008, Genero.TECNOLOGIA, true);
    }

    private LivroResponseDTO criarLivro(LivroRequestDTO request) {
        return restTemplate.postForEntity(url("/livros"), request, LivroResponseDTO.class).getBody();
    }

    @Nested
    class CriarLivro {

        @Test
        void deveCriarLivroERetornar201() {
            ResponseEntity<LivroResponseDTO> response =
                    restTemplate.postForEntity(url("/livros"), requestValido("9780132350884"), LivroResponseDTO.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isNotBlank();
            assertThat(response.getBody().titulo()).isEqualTo("Clean Code");
        }

        @Test
        void deveRetornar422ParaIsbnDuplicado() {
            restTemplate.postForEntity(url("/livros"), requestValido("9780132350884"), LivroResponseDTO.class);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url("/livros"), requestValido("9780132350884"), String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(422));
        }
    }

    @Nested
    class BuscarPorId {

        @Test
        void deveBuscarLivroPorId() {
            LivroResponseDTO criado = criarLivro(requestValido("9780132350884"));

            ResponseEntity<LivroResponseDTO> response =
                    restTemplate.getForEntity(url("/livros/" + criado.id()), LivroResponseDTO.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().id()).isEqualTo(criado.id());
        }

        @Test
        void devePopularCacheAposPrimeiraBusca() {
            LivroResponseDTO criado = criarLivro(requestValido("9780132350884"));

            restTemplate.getForEntity(url("/livros/" + criado.id()), LivroResponseDTO.class);

            var cache = cacheManager.getCache("livro");
            assertThat(cache).isNotNull();
            assertThat(cache.get(criado.id())).isNotNull();
        }

        @Test
        void deveRetornar404ParaIdInexistente() {
            ResponseEntity<String> response =
                    restTemplate.getForEntity(url("/livros/id-inexistente-000"), String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(422));
        }
    }

    @Nested
    class ListarLivros {

        @Test
        void deveListarLivrosComPaginacao() {
            criarLivro(requestValido("9780132350884"));
            criarLivro(requestValido("9780201633610"));

            ResponseEntity<PageResponseDTO<LivroResponseDTO>> response = restTemplate.exchange(
                    url("/livros?pagina=0&tamanho=10"),
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().totalElements()).isEqualTo(2);
        }

        @Test
        void deveListarLivrosFiltradosPorGenero() {
            criarLivro(requestValido("9780132350884"));
            criarLivro(new LivroRequestDTO("Dom Quixote", "Cervantes", "9780201633610", 1605, Genero.ROMANCE, true));

            ResponseEntity<PageResponseDTO<LivroResponseDTO>> response = restTemplate.exchange(
                    url("/livros?pagina=0&tamanho=10&genero=TECNOLOGIA"),
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().totalElements()).isEqualTo(1);
            assertThat(response.getBody().content().get(0).genero()).isEqualTo(Genero.TECNOLOGIA);
        }
    }

    @Nested
    class AtualizarLivro {

        @Test
        void deveAtualizarLivroEInvalidarCache() {
            LivroResponseDTO criado = criarLivro(requestValido("9780132350884"));
            restTemplate.getForEntity(url("/livros/" + criado.id()), LivroResponseDTO.class);

            assertThat(cacheManager.getCache("livro").get(criado.id())).isNotNull();

            LivroRequestDTO atualizado = new LivroRequestDTO("Clean Code 2nd Ed", "Robert C. Martin", "9780132350884", 2020, Genero.TECNOLOGIA, true);
            ResponseEntity<LivroResponseDTO> response = restTemplate.exchange(
                    url("/livros/" + criado.id()), HttpMethod.PUT,
                    new HttpEntity<>(atualizado), LivroResponseDTO.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().titulo()).isEqualTo("Clean Code 2nd Ed");
            assertThat(cacheManager.getCache("livro").get(criado.id())).isNull();
        }

        @Test
        void deveRetornar422AoAtualizarIdInexistente() {
            ResponseEntity<String> response = restTemplate.exchange(
                    url("/livros/id-inexistente-000"), HttpMethod.PUT,
                    new HttpEntity<>(requestValido("9780132350884")), String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(422));
        }
    }

    @Nested
    class DeletarLivro {

        @Test
        void deveDeletarLivroEInvalidarCache() {
            LivroResponseDTO criado = criarLivro(requestValido("9780132350884"));
            restTemplate.getForEntity(url("/livros/" + criado.id()), LivroResponseDTO.class);

            assertThat(cacheManager.getCache("livro").get(criado.id())).isNotNull();

            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                    url("/livros/" + criado.id()), HttpMethod.DELETE, null, Void.class);

            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(livroRepository.findById(criado.id())).isEmpty();
            assertThat(cacheManager.getCache("livro").get(criado.id())).isNull();
        }

        @Test
        void deveRetornar422AoDeletarIdInexistente() {
            ResponseEntity<String> response = restTemplate.exchange(
                    url("/livros/id-inexistente-000"), HttpMethod.DELETE, null, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(422));
        }
    }
}
