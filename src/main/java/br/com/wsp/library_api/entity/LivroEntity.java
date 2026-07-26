package br.com.wsp.library_api.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class LivroEntity {

    public Long id;
    public String titulo;
    public String autor;
    public  String isbn;
    public Integer anoPublicacao;
    public String genero;
    public Boolean disponivel;
    public LocalDateTime dataInclusao;
    public LocalDateTime dataAtualizacao;
}
