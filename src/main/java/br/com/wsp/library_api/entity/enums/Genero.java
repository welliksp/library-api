package br.com.wsp.library_api.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genero {
    FICCAO_CIENTIFICA("Ficção Científica"),
    FANTASIA("Fantasia"),
    ROMANCE("Romance"),
    TERROR("Terror"),
    BIOGRAFIA("Biografia"),
    HISTORIA("História"),
    TECNOLOGIA("Tecnologia"),
    INFANTIL("Infantil");

    private final String descricao;

    Genero(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
    @JsonCreator
    public static Genero fromValue(String value) {
        for (Genero genero : Genero.values()) {
            if (genero.descricao.equalsIgnoreCase(value) || genero.name().equalsIgnoreCase(value)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Gênero inválido: " + value +
                ". Valores permitidos: " + String.join(", ", getDescriptions()));
    }

    public static String[] getDescriptions() {
        return java.util.Arrays.stream(Genero.values())
                .map(Genero::getDescricao)
                .toArray(String[]::new);
    }
}
