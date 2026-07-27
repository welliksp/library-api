package br.com.wsp.library.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ContainerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // MongoDB testcontainer (uses official image provided by testcontainers)
    static final MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:6.0.6"));

    // Redis doesn't require a specific Testcontainers module; use GenericContainer
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine")).withExposedPorts(6379);

    static {
        // start containers before Spring context initialization
        mongo.start();
        redis.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // MongoDB: set the canonical URI
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        // ensure test database is isolated
        registry.add("spring.data.mongodb.database", () -> "library_db_test");

        // Redis: set host/port to point to the container
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getFirstMappedPort());
        // application.yaml uses spring.data.redis as well, set both to be safe
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getFirstMappedPort());

        // tests use no password for redis container
        registry.add("spring.redis.password", () -> "");
        registry.add("spring.data.redis.password", () -> "");
    }

    @Test
    void shouldStartApplicationWithMongoAndRedisContainers() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/livros", String.class);
        // Controller returns 200 even if DB is empty; assert that the app responded
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
