package br.com.wsp.library.api.entity;

import br.com.wsp.library.api.entity.enums.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "livros")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LivroEntity {

    @Id
    public String id;
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @TextIndexed(weight = 2)
    @Field("titulo")
    public String titulo;
    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 100, message = "Autor deve ter no máximo 100 caracteres")
    @TextIndexed(weight = 1)
    @Field("autor")
    public String autor;
    @NotBlank(message = "ISBN é obrigatório")
    @Pattern(regexp = "^(?:\\d{10}|\\d{13})$",
            message = "ISBN deve ter 10 ou 13 dígitos numéricos")
    @Indexed(unique = true)
    @Field("isbn")
    public String isbn;
    @Field("ano_publicacao")
    public Integer anoPublicacao;
    @Indexed
    @Field("genero")
    public Genero genero;
    public Boolean disponivel;
    @Field("data_inclusao")

    public LocalDateTime dataInclusao;
    @Field("data_atualizacao")
    @LastModifiedDate
    public LocalDateTime dataAtualizacao;
}
