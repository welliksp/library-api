package br.com.wsp.library.api.controller.impl;


import br.com.wsp.library.api.controller.LivroEntryPoint;
import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.enums.Genero;
import br.com.wsp.library.api.exception.NegocioException;
import br.com.wsp.library.api.service.ILivroService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
@Slf4j
@RestController
@AllArgsConstructor
public class LivroController implements LivroEntryPoint {

    private final ILivroService livroService;

    @Override
    public ResponseEntity<LivroResponseDTO> criarLivro(LivroRequestDTO request) {

        log.info("Recebida requisição POST /livros - Dados: {}", request);

        LivroResponseDTO response = livroService.criarLivro(request);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Override
    public ResponseEntity<LivroResponseDTO> buscarPorId(String id) {

        LivroResponseDTO livro = livroService.buscarPorId(id);

        return ResponseEntity.ok(livro);
    }

    @Override
    public ResponseEntity<PageResponseDTO<LivroResponseDTO>> listarLivros(Genero genero, Integer pagina, Integer tamanho) {

        log.info("Recebida requisição GET /livros - Página: {}, Tamanho: {}, Gênero: {}",
                pagina, tamanho, genero);

        if (pagina < 0) {
            throw new NegocioException("PAGINACAO_INVALIDA", "Página não pode ser negativa.");
        }
        if (tamanho < 1 || tamanho > 50) {
            throw new NegocioException("PAGINACAO_INVALIDA", "Tamanho deve ser entre 1 e 50.");
        }

        PageResponseDTO<LivroResponseDTO> response = livroService.listarLivros(
                genero,
                pagina,
                tamanho
        );

        log.info("Retornando página {} de {} - Total de elementos: {}, Total de páginas: {}",
                response.page(),
                response.totalPages(),
                response.totalElements(),
                response.totalPages());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LivroResponseDTO> atualizarLivro(String id, LivroRequestDTO request) {


        log.info("Recebida requisição PUT /livros/{}", id);
        log.debug("Dados recebidos: {}", request);

        LivroResponseDTO response = livroService.atualizarLivro(id, request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deletarLivro(String id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }
}
