package br.com.wsp.library.api.controller.impl;


import br.com.wsp.library.api.controller.LivroEntryPoint;
import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.service.ILivroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class LivroController implements LivroEntryPoint {

    private final ILivroService livroService;

    public LivroController(ILivroService livroService) {
        this.livroService = livroService;
    }

    @Override
    public ResponseEntity<LivroResponseDTO> criarLivro(LivroRequestDTO request) {
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
}
