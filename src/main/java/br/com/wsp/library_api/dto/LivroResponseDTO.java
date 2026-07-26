package br.com.wsp.library_api.dto;

import br.com.wsp.library_api.entity.enums.Genero;

import java.time.LocalDateTime;

public record LivroResponseDTO(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Genero genero,
        Boolean disponivel,
        LocalDateTime dataInclusao,
        LocalDateTime dataAtualizacao
) {
}