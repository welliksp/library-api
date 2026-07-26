package br.com.wsp.library.api.exception;

import lombok.Getter;

@Getter
public class NegocioException extends RuntimeException {

    private final String codigo;

    public NegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public NegocioException(String codigo, String mensagem, Throwable cause) {
        super(mensagem, cause);
        this.codigo = codigo;
    }

    public static final String LIVRO_NAO_ENCONTRADO = "LIVRO_NAO_ENCONTRADO";
    public static final String LIVRO_ISBN_DUPLICADO = "LIVRO_ISBN_DUPLICADO";
    public static final String GENERO_INVALIDO = "GENERO_INVALIDO";
    public static final String ANO_PUBLICACAO_INVALIDO = "ANO_PUBLICACAO_INVALIDO";
    public static final String CAMPOS_OBRIGATORIOS = "CAMPOS_OBRIGATORIOS";
}