package com.proyecto.blog.service;

import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.model.Author;

import java.util.List;
import java.util.Optional;

public interface IAuthorService {

    AuthorDTO createAuthor(Long userId); // Crear un nuevo autor
    Optional<AuthorDTO> getAuthorById(Long id); // Obtener un autor por ID
    List<AuthorDTO> getAllAuthors(); // Obtener todos los autores
    AuthorDTO updateAuthor(Long id, Author authorDetails); // Actualizar un autor existente
    boolean deleteAuthor(Long id); // Eliminar un autor

    Optional<Author> getAuthorEntityById(Long id); // Nuevo método para obtener la entidad real


}
