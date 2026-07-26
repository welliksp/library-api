package br.com.wsp.library.api.controller;

import br.com.wsp.library.api.dto.ErrorResponseDTO;
import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.enums.Genero;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/livros")
@Tag(name = "Livros", description = "API para gerenciamento de livros da biblioteca")
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
    public ResponseEntity<LivroResponseDTO> criarLivro(@Valid @RequestBody LivroRequestDTO request);

    @Operation(
            summary = "Buscar livro por ID",
            description = "Busca um livro específico pelo seu ID. Utiliza cache Redis com TTL de 10 minutos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Livro encontrado",
                    content = @Content(schema = @Schema(implementation = LivroResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscarPorId(@Parameter(description = "ID do livro", required = true) @PathVariable String id);

    @Operation(
            summary = "Listar todos os livros com paginação",
            description = """
                    Lista todos os livros com suporte a paginação e filtro opcional por gênero.
                    
                    **Características:**
                    - Paginação obrigatória (padrão: página 0, tamanho 10)
                    - Filtro opcional por gênero
                    - Cache Redis com TTL de 5 minutos
                    - Ordenação padrão por título (A-Z)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de livros retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros de paginação inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PageResponseDTO<LivroResponseDTO>> listarLivros(
            @Parameter(
                    description = "Filtro por gênero do livro",
                    example = "FANTASIA",
                    schema = @Schema(implementation = Genero.class)
            )
            @RequestParam(required = false) Genero genero,

            @Parameter(
                    description = "Número da página (inicia em 0)",
                    example = "0",
                    required = true
            )
            @RequestParam(defaultValue = "0") Integer pagina,

            @Parameter(
                    description = "Quantidade de itens por página (mínimo 1, máximo 50)",
                    example = "10",
                    required = true
            )
            @RequestParam(defaultValue = "10") Integer tamanho);

    @Operation(
            summary = "Atualizar um livro existente",
            description = """
                    Atualiza os dados de um livro pelo ID.
                    
                    **Regras:**
                    - Todas as validações da criação se aplicam
                    - ISBN deve ser único (exceto se for o mesmo)
                    - Invalida automaticamente o cache Redis
                    - Cache invalidado: biblioteca:livro:{id}
                    - Cache de listagem também é invalidado
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Livro atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ISBN duplicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação de negócio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizarLivro(
            @Parameter(
                    description = "ID do livro a ser atualizado",
                    required = true,
                    example = "67f8a1b2c3d4e5f6a7b8c9d0"
            )
            @PathVariable String id,

            @Valid @RequestBody LivroRequestDTO request);
}
