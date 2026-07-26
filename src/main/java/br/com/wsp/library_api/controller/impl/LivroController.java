package br.com.wsp.library_api.controller.impl;


import br.com.wsp.library_api.controller.LivroEntryPoint;
import br.com.wsp.library_api.dto.LivroRequestDTO;
import br.com.wsp.library_api.dto.LivroResponseDTO;
import br.com.wsp.library_api.service.ILivroService;
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
}
