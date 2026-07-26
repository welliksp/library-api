package br.com.wsp.library_api.repository;

import br.com.wsp.library_api.entity.LivroEntity;
import br.com.wsp.library_api.entity.enums.Genero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LivroRepository extends MongoRepository<LivroEntity, Long> {


    Optional<LivroEntity> findByIsbn(String isbn);

    Page<LivroEntity> findByGenero(Genero genero, Pageable pageable);

    boolean existsByIsbn(String isbn);

}
