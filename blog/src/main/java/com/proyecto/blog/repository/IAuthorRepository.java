package com.proyecto.blog.repository;

import com.proyecto.blog.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAuthorRepository extends JpaRepository<Author, Long> {

    // Método para encontrar autores que no han sido eliminados
    List<Author> findByDeletedFalse();

    // Método para buscar un autor por ID si no ha sido eliminado
    Optional<Author> findByIdAndDeletedFalse(Long id);

}
