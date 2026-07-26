package br.com.wsp.library_api.dto;

import br.com.wsp.library_api.entity.LivroEntity;
import br.com.wsp.library_api.entity.enums.Genero;

import java.time.LocalDateTime;

public record LivroResponseDTO(
        String id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Genero genero,
        Boolean disponivel,
        LocalDateTime dataInclusao,
        LocalDateTime dataAtualizacao
) {
    public static LivroResponseDTO fromEntity(LivroEntity entity) {
        return new LivroResponseDTO(
                entity.getId(),
                entity.getTitulo(),
                entity.getAutor(),
                entity.getIsbn(),
                entity.getAnoPublicacao(),
                entity.getGenero(),
                entity.getDisponivel(),
                entity.getDataInclusao(),
                entity.getDataAtualizacao()
        );
    }
}