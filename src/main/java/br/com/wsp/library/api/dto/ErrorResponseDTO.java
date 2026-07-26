package br.com.wsp.library.api.dto;


import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String codigo,
        String mensagem,
        LocalDateTime timestamp
) {
    public ErrorResponseDTO(String codigo, String mensagem) {
        this(codigo, mensagem, LocalDateTime.now());
    }
}