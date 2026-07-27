# Library API

API REST para gerenciamento de uma biblioteca de livros — projeto de teste técnico.

Tecnologias
- Java 21
- Spring Boot
- Spring Data MongoDB
- Spring Data Redis (Spring Cache)
- Maven
- Lombok
- ModelMapper
- SpringDoc OpenAPI
- JUnit 5, Mockito, Testcontainers (integração)

Pré-requisitos
- Java 21
- Maven 3.9+
- Docker (necessário para rodar testes de integração que usam Testcontainers)

Executando a aplicação
1. Configurar variáveis (opcional):
   - spring.data.mongodb.uri
   - spring.data.redis.host / spring.data.redis.port
2. Rodar com Maven:

   mvn spring-boot:run

Executando testes
- Unitários: `mvn test`
- Integração (Testcontainers): exige Docker ativo; `mvn test` também executa testes de integração por configuração do projeto.

Endpoints principais
- POST /livros
  - Cria um livro. Validações: titulo, autor, isbn, genero, anoPublicacao.
- GET /livros/{id}
  - Busca por ID. Usa cache Redis (chave: biblioteca:livro:{id}, TTL: 10 minutos).
- GET /livros?pagina=0&tamanho=10&genero=TECNOLOGIA
  - Lista paginada com filtro opcional por gênero.
- PUT /livros/{id}
  - Atualiza livro. Invalida cache do id e da listagem.
- DELETE /livros/{id}
  - Remove livro. Invalida cache do id e da listagem.

Cache Redis
- Provider: Spring Cache com Redis
- Chave padrão: `biblioteca:livro:{id}`
- TTL livro: 10 minutos
- TTL listagem (cache `livros`): 5 minutos
- Operações PUT e DELETE invalidam cache relevante via `@CacheEvict`

Documentação OpenAPI / Swagger
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Observações
- DTOs usam records (Java 21). Entidade anotada com `@Document`.
- Erros de negócio via `NegocioException` e tratados por `GlobalExceptionHandler` com payload padronizado.
- ISBN validado por regex que aceita exatamente 10 ou 13 dígitos.

Se quiser que a README inclua exemplos de requests/responses, cole amostras aqui e eu adiciono.