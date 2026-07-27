package br.com.wsp.library.api.controller.impl;

import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.enums.Genero;
import br.com.wsp.library.api.exception.GlobalExceptionHandler;
import br.com.wsp.library.api.exception.NegocioException;
import br.com.wsp.library.api.service.ILivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("LivroController - Testes Unitários")
class LivroControllerTest {

    @Mock
    private ILivroService livroService;

    @InjectMocks
    private LivroController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private LivroResponseDTO livroResponse;
    private LivroRequestDTO livroRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        livroResponse = new LivroResponseDTO(
                "6a666cde0a4845b2dcd257a6",
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                Genero.TECNOLOGIA,
                true,
                LocalDateTime.now(),
                null
        );

        livroRequest = new LivroRequestDTO(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2008,
                Genero.TECNOLOGIA,
                true
        );
    }

    @Nested
    @DisplayName("POST /livros")
    class CriarLivro {

        @Test
        @DisplayName("deve retornar 201 com header Location ao criar livro")
        void deveRetornar201ComHeaderLocation() throws Exception {
            when(livroService.criarLivro(any(LivroRequestDTO.class))).thenReturn(livroResponse);

            mockMvc.perform(post("/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(livroRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id", is("6a666cde0a4845b2dcd257a6")))
                    .andExpect(jsonPath("$.titulo", is("Clean Code")))
                    .andExpect(jsonPath("$.isbn", is("9780132350884")));

            verify(livroService).criarLivro(any(LivroRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando campos obrigatórios ausentes")
        void deveRetornar400QuandoCamposAusentes() throws Exception {
            var bodyInvalido = new LivroRequestDTO(null, null, null, null, null, null);

            mockMvc.perform(post("/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.codigo", is("DADOS_INVALIDOS")))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));

            verify(livroService, never()).criarLivro(any());
        }

        @Test
        @DisplayName("deve retornar 422 quando ISBN duplicado")
        void deveRetornar422QuandoIsbnDuplicado() throws Exception {
            when(livroService.criarLivro(any(LivroRequestDTO.class)))
                    .thenThrow(new NegocioException(NegocioException.LIVRO_ISBN_DUPLICADO,
                            "ISBN '9780132350884' já está cadastrado no sistema."));

            mockMvc.perform(post("/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(livroRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.codigo", is("LIVRO_ISBN_DUPLICADO")))
                    .andExpect(jsonPath("$.mensagem", notNullValue()));
        }
    }

    @Nested
    @DisplayName("GET /livros/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 com livro quando id existir")
        void deveRetornar200QuandoIdExistir() throws Exception {
            when(livroService.buscarPorId("6a666cde0a4845b2dcd257a6")).thenReturn(livroResponse);

            mockMvc.perform(get("/livros/{id}", "6a666cde0a4845b2dcd257a6"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("6a666cde0a4845b2dcd257a6")))
                    .andExpect(jsonPath("$.titulo", is("Clean Code")))
                    .andExpect(jsonPath("$.autor", is("Robert C. Martin")));

            verify(livroService).buscarPorId("6a666cde0a4845b2dcd257a6");
        }

        @Test
        @DisplayName("deve retornar 404 quando id não encontrado")
        void deveRetornar404QuandoIdNaoEncontrado() throws Exception {
            when(livroService.buscarPorId("id-inexistente"))
                    .thenThrow(new NegocioException(NegocioException.LIVRO_NAO_ENCONTRADO,
                            "Livro com id 'id-inexistente' não encontrado."));

            mockMvc.perform(get("/livros/{id}", "id-inexistente"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.codigo", is("LIVRO_NAO_ENCONTRADO")));
        }
    }

    @Nested
    @DisplayName("GET /livros")
    class ListarLivros {

        @Test
        @DisplayName("deve retornar 200 com página de livros")
        void deveRetornar200ComPaginaDeLivros() throws Exception {
            var pageResponse = new PageResponseDTO<>(List.of(livroResponse), 0, 10, 1L, 1, true, true);
            when(livroService.listarLivros(null, 0, 10)).thenReturn(pageResponse);

            mockMvc.perform(get("/livros")
                            .param("pagina", "0")
                            .param("tamanho", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.content[0].titulo", is("Clean Code")));

            verify(livroService).listarLivros(null, 0, 10);
        }

        @Test
        @DisplayName("deve retornar 200 com filtro por gênero")
        void deveRetornar200ComFiltroPorGenero() throws Exception {
            var pageResponse = new PageResponseDTO<>(List.of(livroResponse), 0, 10, 1L, 1, true, true);
            when(livroService.listarLivros(eq(Genero.TECNOLOGIA), eq(0), eq(10))).thenReturn(pageResponse);

            mockMvc.perform(get("/livros")
                            .param("genero", "TECNOLOGIA")
                            .param("pagina", "0")
                            .param("tamanho", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].genero", is("Tecnologia")));
        }

        @Test
        @DisplayName("deve retornar 422 quando página negativa")
        void deveRetornar422QuandoPaginaNegativa() throws Exception {
            mockMvc.perform(get("/livros")
                            .param("pagina", "-1")
                            .param("tamanho", "10"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.codigo", is("PAGINACAO_INVALIDA")));

            verify(livroService, never()).listarLivros(any(), any(), any());
        }

        @Test
        @DisplayName("deve retornar 422 quando tamanho maior que 50")
        void deveRetornar422QuandoTamanhoMaiorQue50() throws Exception {
            mockMvc.perform(get("/livros")
                            .param("pagina", "0")
                            .param("tamanho", "100"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.codigo", is("PAGINACAO_INVALIDA")));

            verify(livroService, never()).listarLivros(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("PUT /livros/{id}")
    class AtualizarLivro {

        @Test
        @DisplayName("deve retornar 200 ao atualizar livro com sucesso")
        void deveRetornar200AoAtualizarLivro() throws Exception {
            when(livroService.atualizarLivro(eq("6a666cde0a4845b2dcd257a6"), any(LivroRequestDTO.class)))
                    .thenReturn(livroResponse);

            mockMvc.perform(put("/livros/{id}", "6a666cde0a4845b2dcd257a6")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(livroRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("6a666cde0a4845b2dcd257a6")))
                    .andExpect(jsonPath("$.titulo", is("Clean Code")));

            verify(livroService).atualizarLivro(eq("6a666cde0a4845b2dcd257a6"), any(LivroRequestDTO.class));
        }

        @Test
        @DisplayName("deve retornar 422 quando livro não encontrado")
        void deveRetornar422QuandoLivroNaoEncontrado() throws Exception {
            when(livroService.atualizarLivro(eq("id-inexistente"), any(LivroRequestDTO.class)))
                    .thenThrow(new NegocioException(NegocioException.LIVRO_NAO_ENCONTRADO,
                            "Livro com id 'id-inexistente' não encontrado."));

            mockMvc.perform(put("/livros/{id}", "id-inexistente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(livroRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.codigo", is("LIVRO_NAO_ENCONTRADO")));
        }

        @Test
        @DisplayName("deve retornar 400 quando body inválido")
        void deveRetornar400QuandoBodyInvalido() throws Exception {
            var bodyInvalido = new LivroRequestDTO(null, null, null, null, null, null);

            mockMvc.perform(put("/livros/{id}", "6a666cde0a4845b2dcd257a6")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.codigo", is("DADOS_INVALIDOS")));

            verify(livroService, never()).atualizarLivro(any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /livros/{id}")
    class DeletarLivro {

        @Test
        @DisplayName("deve retornar 204 ao deletar livro com sucesso")
        void deveRetornar204AoDeletarLivro() throws Exception {
            doNothing().when(livroService).deletarLivro("6a666cde0a4845b2dcd257a6");

            mockMvc.perform(delete("/livros/{id}", "6a666cde0a4845b2dcd257a6"))
                    .andExpect(status().isNoContent());

            verify(livroService).deletarLivro("6a666cde0a4845b2dcd257a6");
        }

        @Test
        @DisplayName("deve retornar 422 quando livro não encontrado")
        void deveRetornar422QuandoLivroNaoEncontrado() throws Exception {
            doThrow(new NegocioException(NegocioException.LIVRO_NAO_ENCONTRADO,
                    "Livro com id 'id-inexistente' não encontrado."))
                    .when(livroService).deletarLivro("id-inexistente");

            mockMvc.perform(delete("/livros/{id}", "id-inexistente"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.codigo", is("LIVRO_NAO_ENCONTRADO")))
                    .andExpect(jsonPath("$.mensagem", notNullValue()))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }
    }
}
