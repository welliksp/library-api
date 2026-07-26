package br.com.wsp.library_api.dto;


import br.com.wsp.library_api.entity.enums.Genero;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Year;

public record LivroRequestDTO(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
        String titulo,

        @NotBlank(message = "Autor é obrigatório")
        @Size(max = 100, message = "Autor deve ter no máximo 100 caracteres")
        String autor,

        @NotBlank(message = "ISBN é obrigatório")
        @Pattern(regexp = "^[0-9]{10}|[0-9]{13}$",
                message = "ISBN deve ter 10 ou 13 dígitos numéricos")
        String isbn,

        @NotNull(message = "Ano de publicação é obrigatório")
        @Min(value = 1000, message = "Ano de publicação deve ser maior que 1000")
        @Max(value = 2100, message = "Ano de publicação deve ser menor ou igual a 2100")
        Integer anoPublicacao,

        @NotNull(message = "Gênero é obrigatório")
        Genero genero,

        @NotNull(message = "Disponibilidade é obrigatória")
        Boolean disponivel
) {
    @AssertTrue(message = "Ano de publicação não pode ser no futuro")
    public boolean isAnoPublicacaoValido() {
        if (anoPublicacao == null) return true;
        return anoPublicacao <= Year.now().getValue();
    }
}