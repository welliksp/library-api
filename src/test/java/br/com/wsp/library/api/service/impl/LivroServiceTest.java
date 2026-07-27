package br.com.wsp.library.api.service.impl;

import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.LivroEntity;
import br.com.wsp.library.api.entity.enums.Genero;
import br.com.wsp.library.api.exception.NegocioException;
import br.com.wsp.library.api.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private LivroService service;

    private LivroEntity livroEntity;
    private LivroRequestDTO livroRequest;

    @BeforeEach
    void setUp() {
        livroEntity = LivroEntity.builder()
                .id("6a666cde0a4845b2dcd257a6")
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .isbn("9780132350884")
                .anoPublicacao(2008)
                .genero(Genero.TECNOLOGIA)
                .disponivel(true)
                .dataInclusao(LocalDateTime.now())
                .build();

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
    @DisplayName("criarLivro")
    class CriarLivro {

        @Test
        @DisplayName("deve criar livro com sucesso")
        void deveCriarLivroComSucesso() {
            when(repository.existsByIsbn(livroRequest.isbn())).thenReturn(false);
            when(mapper.map(livroRequest, LivroEntity.class)).thenReturn(livroEntity);
            when(repository.save(any(LivroEntity.class))).thenReturn(livroEntity);

            LivroResponseDTO response = service.criarLivro(livroRequest);

            assertThat(response).isNotNull();
            assertThat(response.titulo()).isEqualTo("Clean Code");
            assertThat(response.isbn()).isEqualTo("9780132350884");
            verify(repository).save(any(LivroEntity.class));
        }

        @Test
        @DisplayName("deve lançar NegocioException quando ISBN já cadastrado")
        void deveLancarExcecaoQuandoIsbnDuplicado() {
            when(repository.existsByIsbn(livroRequest.isbn())).thenReturn(true);

            assertThatThrownBy(() -> service.criarLivro(livroRequest))
                    .isInstanceOf(NegocioException.class)
                    .hasMessageContaining("9780132350884")
                    .extracting("codigo").isEqualTo(NegocioException.LIVRO_ISBN_DUPLICADO);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando ano de publicação no futuro")
        void deveLancarExcecaoQuandoAnoNoFuturo() {
            LivroRequestDTO requestAnoFuturo = new LivroRequestDTO(
                    "Livro Futuro", "Autor", "9780132350884", 2099, Genero.TECNOLOGIA, true
            );

            assertThatThrownBy(() -> service.criarLivro(requestAnoFuturo))
                    .isInstanceOf(NegocioException.class)
                    .hasMessageContaining("futuro")
                    .extracting("codigo").isEqualTo(NegocioException.ANO_PUBLICACAO_INVALIDO);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando ano de publicação menor que 1000")
        void deveLancarExcecaoQuandoAnoMenorQue1000() {
            LivroRequestDTO requestAnoInvalido = new LivroRequestDTO(
                    "Livro Antigo", "Autor", "9780132350884", 999, Genero.TECNOLOGIA, true
            );

            assertThatThrownBy(() -> service.criarLivro(requestAnoInvalido))
                    .isInstanceOf(NegocioException.class)
                    .hasMessageContaining("1000")
                    .extracting("codigo").isEqualTo(NegocioException.ANO_PUBLICACAO_INVALIDO);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve definir disponivel como true quando não informado")
        void deveDefinirDisponivelComoTrueQuandoNaoInformado() {
            LivroEntity livroSemDisponivel = LivroEntity.builder()
                    .titulo("Clean Code")
                    .autor("Robert C. Martin")
                    .isbn("9780132350884")
                    .anoPublicacao(2008)
                    .genero(Genero.TECNOLOGIA)
                    .disponivel(null)
                    .build();

            when(repository.existsByIsbn(any())).thenReturn(false);
            when(mapper.map(any(), eq(LivroEntity.class))).thenReturn(livroSemDisponivel);
            when(repository.save(any(LivroEntity.class))).thenAnswer(inv -> {
                LivroEntity e = inv.getArgument(0);
                e.setId("abc123");
                return e;
            });

            service.criarLivro(livroRequest);

            assertThat(livroSemDisponivel.getDisponivel()).isTrue();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar livro quando id existir")
        void deveRetornarLivroQuandoIdExistir() {
            when(repository.findById("6a666cde0a4845b2dcd257a6")).thenReturn(Optional.of(livroEntity));

            LivroResponseDTO response = service.buscarPorId("6a666cde0a4845b2dcd257a6");

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("6a666cde0a4845b2dcd257a6");
            assertThat(response.titulo()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("deve lançar NegocioException quando id não encontrado")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId("id-inexistente"))
                    .isInstanceOf(NegocioException.class)
                    .extracting("codigo").isEqualTo(NegocioException.LIVRO_NAO_ENCONTRADO);
        }
    }

    @Nested
    @DisplayName("listarLivros")
    class ListarLivros {

        @Test
        @DisplayName("deve listar todos os livros sem filtro de gênero")
        void deveListarTodosOsLivrosSemFiltro() {
            var page = new PageImpl<>(List.of(livroEntity), PageRequest.of(0, 10), 1);
            when(repository.findAll(any(Pageable.class))).thenReturn(page);

            PageResponseDTO<LivroResponseDTO> response = service.listarLivros(null, 0, 10);

            assertThat(response.content()).hasSize(1);
            assertThat(response.totalElements()).isEqualTo(1);
            verify(repository).findAll(any(Pageable.class));
            verify(repository, never()).findByGenero(any(), any());
        }

        @Test
        @DisplayName("deve listar livros filtrados por gênero")
        void deveListarLivrosFiltradosPorGenero() {
            var page = new PageImpl<>(List.of(livroEntity), PageRequest.of(0, 10), 1);
            when(repository.findByGenero(eq(Genero.TECNOLOGIA), any(Pageable.class))).thenReturn(page);

            PageResponseDTO<LivroResponseDTO> response = service.listarLivros(Genero.TECNOLOGIA, 0, 10);

            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).genero()).isEqualTo(Genero.TECNOLOGIA);
            verify(repository).findByGenero(eq(Genero.TECNOLOGIA), any(Pageable.class));
            verify(repository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("deve retornar página vazia quando não houver livros")
        void deveRetornarPaginaVaziaQuandoNaoHouverLivros() {
            var page = new PageImpl<LivroEntity>(List.of(), PageRequest.of(0, 10), 0);
            when(repository.findAll(any(Pageable.class))).thenReturn(page);

            PageResponseDTO<LivroResponseDTO> response = service.listarLivros(null, 0, 10);

            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("atualizarLivro")
    class AtualizarLivro {

        @Test
        @DisplayName("deve atualizar livro com sucesso")
        void deveAtualizarLivroComSucesso() {
            when(repository.findById("6a666cde0a4845b2dcd257a6")).thenReturn(Optional.of(livroEntity));
            when(repository.save(any(LivroEntity.class))).thenReturn(livroEntity);

            LivroResponseDTO response = service.atualizarLivro("6a666cde0a4845b2dcd257a6", livroRequest);

            assertThat(response).isNotNull();
            assertThat(response.titulo()).isEqualTo("Clean Code");
            verify(repository).save(any(LivroEntity.class));
        }

        @Test
        @DisplayName("deve lançar NegocioException quando livro não encontrado")
        void deveLancarExcecaoQuandoLivroNaoEncontrado() {
            when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizarLivro("id-inexistente", livroRequest))
                    .isInstanceOf(NegocioException.class)
                    .extracting("codigo").isEqualTo(NegocioException.LIVRO_NAO_ENCONTRADO);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar NegocioException quando novo ISBN já está em uso")
        void deveLancarExcecaoQuandoNovoIsbnJaEmUso() {
            LivroRequestDTO requestNovoIsbn = new LivroRequestDTO(
                    "Clean Code", "Robert C. Martin", "9780134685991", 2008, Genero.TECNOLOGIA, true
            );

            when(repository.findById("6a666cde0a4845b2dcd257a6")).thenReturn(Optional.of(livroEntity));
            when(repository.existsByIsbn("9780134685991")).thenReturn(true);

            assertThatThrownBy(() -> service.atualizarLivro("6a666cde0a4845b2dcd257a6", requestNovoIsbn))
                    .isInstanceOf(NegocioException.class)
                    .extracting("codigo").isEqualTo(NegocioException.LIVRO_ISBN_DUPLICADO);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("não deve validar ISBN quando for o mesmo do livro existente")
        void naoDeveValidarIsbnQuandoForOMesmo() {
            when(repository.findById("6a666cde0a4845b2dcd257a6")).thenReturn(Optional.of(livroEntity));
            when(repository.save(any(LivroEntity.class))).thenReturn(livroEntity);

            service.atualizarLivro("6a666cde0a4845b2dcd257a6", livroRequest);

            verify(repository, never()).existsByIsbn(any());
        }
    }

    @Nested
    @DisplayName("deletarLivro")
    class DeletarLivro {

        @Test
        @DisplayName("deve deletar livro com sucesso")
        void deveDeletarLivroComSucesso() {
            when(repository.findById("6a666cde0a4845b2dcd257a6")).thenReturn(Optional.of(livroEntity));

            service.deletarLivro("6a666cde0a4845b2dcd257a6");

            verify(repository).delete(livroEntity);
        }

        @Test
        @DisplayName("deve lançar NegocioException quando livro não encontrado")
        void deveLancarExcecaoQuandoLivroNaoEncontrado() {
            when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deletarLivro("id-inexistente"))
                    .isInstanceOf(NegocioException.class)
                    .extracting("codigo").isEqualTo(NegocioException.LIVRO_NAO_ENCONTRADO);

            verify(repository, never()).delete(any());
        }
    }
}
