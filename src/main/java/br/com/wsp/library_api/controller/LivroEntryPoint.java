package br.com.wsp.library_api.controller;

import br.com.wsp.library_api.dto.ErrorResponseDTO;
import br.com.wsp.library_api.dto.LivroRequestDTO;
import br.com.wsp.library_api.dto.LivroResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/livros")
public interface LivroEntryPoint {
    @Operation(
            summary = "Criar um novo livro",
            description = "Cria um novo livro com validações de negócio. ISBN deve ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Livro criado com sucesso",
                    content = @Content(schema = @Schema(implementation = LivroResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ISBN duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação de negócio",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<LivroResponseDTO> criarLivro(@RequestBody LivroRequestDTO request);
}
