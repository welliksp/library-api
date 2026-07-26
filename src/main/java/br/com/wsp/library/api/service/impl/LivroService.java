package br.com.wsp.library.api.service.impl;


import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.LivroEntity;
import br.com.wsp.library.api.entity.enums.Genero;
import br.com.wsp.library.api.exception.NegocioException;
import br.com.wsp.library.api.exception.NotFoundException;
import br.com.wsp.library.api.repository.LivroRepository;
import br.com.wsp.library.api.service.ILivroService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivroService implements ILivroService {

    public final LivroRepository repository;
    private final ModelMapper mapper;

    @Override
    public LivroResponseDTO criarLivro(LivroRequestDTO request) {

        log.info("Iniciando processo de criação do livro: {}", request);

        validarDadosCriacao(request);

        LivroEntity livro = mapper.map(request, LivroEntity.class);

        if (livro.getDisponivel() == null) {
            livro.setDisponivel(true);
        }
        livro.setDataInclusao(java.time.LocalDateTime.now());

        LivroEntity livroSalvo = repository.save(livro);


        return LivroResponseDTO.fromEntity(livroSalvo);
    }

    @Override
    @Cacheable(
            value = "livro",
            key = "#id",
            unless = "#result == null"
    )
    public LivroResponseDTO buscarPorId(String id) {

        var livro = repository.findById(id).orElseThrow(() -> new NotFoundException("Livro não encontrado"));

        log.info("Livro encontrado no MongoDB - ID: {}, Título: {}",
                livro.getId(), livro.getTitulo());

        return LivroResponseDTO.fromEntity(livro);
    }

    @Override
    @Cacheable(
            value = "livros",
            key = "#genero != null ? 'genero_' + #genero + '_page_' + #pagina + '_size_' + #tamanho : 'all_page_' + #pagina + '_size_' + #tamanho",
            unless = "#result == null || #result.content().isEmpty()"
    )
    public PageResponseDTO<LivroResponseDTO> listarLivros(
            Genero genero,
            Integer pagina,
            Integer tamanho) {

        log.info("Listando livros - Página: {}, Tamanho: {}, Gênero: {}",
                pagina, tamanho, genero);

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("titulo").ascending());

        Page<LivroEntity> page;

        if (genero != null) {
            log.info("Aplicando filtro por gênero: {}", genero);
            page = repository.findByGenero(genero, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        log.info("Livros encontrados: {} de {} total",
                page.getNumberOfElements(), page.getTotalElements());

        Page<LivroResponseDTO> responsePage = page.map(LivroResponseDTO::fromEntity);

        return PageResponseDTO.fromPage(responsePage);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "livro", key = "#id"),
            @CacheEvict(value = "livros", allEntries = true)
    })
    public LivroResponseDTO atualizarLivro(String id, LivroRequestDTO request) {

        log.info("Atualizando livro - ID: {}, Dados recebidos: {}", id, request);
        LivroEntity livro = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("❌ Livro não encontrado para atualização - ID: {}", id);
                    return new NotFoundException("Livro com id '" + id + "' não encontrado para atualização");
                });

        log.info("Livro encontrado - Título atual: {}", livro.getTitulo());
        validarDadosAtualizacao(request, livro);

        atualizarDadosLivro(livro, request);

        LivroEntity livroAtualizado = repository.save(livro);
        log.info("Livro atualizado com sucesso! ID: {}, Título: {}",
                livroAtualizado.getId(), livroAtualizado.getTitulo());

        return LivroResponseDTO.fromEntity(livroAtualizado);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "livro", key = "#id"),
            @CacheEvict(value = "livros", allEntries = true)
    })
    public void deletarLivro(String id) {

        log.info("Deletando livro - ID: {}", id);
        var livro = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Livro não encontrado para deleção - ID: {}", id);
                    return new NotFoundException("Livro com id '" + id + "' não encontrado para deleção");
                });

        repository.delete(livro);
        log.info("Livro deletado com sucesso! ID: {}, Título: {}",
                livro.getId(), livro.getTitulo());
    }

    private void validarDadosAtualizacao(LivroRequestDTO request, LivroEntity livroExistente) {
        validarAnoPublicacao(request.anoPublicacao());

        validarGenero(request.genero());

        if (!livroExistente.getIsbn().equals(request.isbn())) {
            validarIsbnUnico(request.isbn());
        }

    }

    private void atualizarDadosLivro(LivroEntity livro, LivroRequestDTO request) {
        if (request.titulo() != null) {
            livro.setTitulo(request.titulo());
        }
        if (request.autor() != null) {
            livro.setAutor(request.autor());
        }
        if (request.isbn() != null) {
            livro.setIsbn(request.isbn());
        }
        if (request.anoPublicacao() != null) {
            livro.setAnoPublicacao(request.anoPublicacao());
        }
        if (request.genero() != null) {
            livro.setGenero(request.genero());
        }
        if (request.disponivel() != null) {
            livro.setDisponivel(request.disponivel());
        }
        livro.setDataAtualizacao(java.time.LocalDateTime.now());

    }
    private void validarIsbnUnico(String isbn) {
        if (repository.existsByIsbn(isbn)) {
            throw new NegocioException(
                    NegocioException.LIVRO_ISBN_DUPLICADO,
                    "ISBN '" + isbn + "' já está cadastrado no sistema"
            );
        }
    }

    private void validarDadosCriacao(LivroRequestDTO request) {

        validarAnoPublicacao(request.anoPublicacao());

        validarIsbn(request.isbn());

        validarGenero(request.genero());

    }

    private void validarGenero(@NotNull(message = "Gênero é obrigatório") Genero genero) {

        if (genero == null) {
            throw new IllegalArgumentException("Gênero é obrigatório");
        }

        try {
            Genero.fromValue(genero.getDescricao());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Gênero inválido: " + genero + ". Valores permitidos: " + String.join(", ", Genero.getDescriptions()));
        }


    }

    private void validarIsbn(@NotBlank(message = "ISBN é obrigatório") @Pattern(regexp = "^[0-9]{10}|[0-9]{13}$",
            message = "ISBN deve ter 10 ou 13 dígitos numéricos") String isbn) {

        if (repository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Já existe um livro cadastrado com o ISBN informado");
        }


    }

    private void validarAnoPublicacao(@NotNull(message = "Ano de publicação é obrigatório") @Min(value = 1000, message = "Ano de publicação deve ser maior que 1000") @Max(value = 2100, message = "Ano de publicação deve ser menor ou igual a 2100") Integer ano) {

        int anoAtual = Year.now().getValue();
        if (ano < 1000) {
            throw new IllegalArgumentException("Ano de publicação deve ser maior que 1000");
        }

        if (ano > anoAtual) {
            throw new IllegalArgumentException("Ano de publicação não pode ser no futuro");
        }


    }
}
