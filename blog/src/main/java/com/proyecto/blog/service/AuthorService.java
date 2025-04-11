package com.proyecto.blog.service;

import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.dto.PostDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IAuthorRepository;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService implements IAuthorService{

    @Autowired
    private IAuthorRepository authorRepository; // Repositorio de Author

    @Autowired
    private IUserSecRepository userSecRepository; // Repositorio de UserSec para asociar un UserSec al Author

    @Override
    public Optional<Author> getAuthorEntityById(Long id) {
        return authorRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorRepository.findByDeletedFalse();  // Solo obtener autores no eliminados
        return authors.stream()
                .map(this::convertToDTO)  // Convertir cada Author a AuthorDTO
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AuthorDTO> getAuthorById(Long id) {
        Optional<Author> author = authorRepository.findByIdAndDeletedFalse(id);  // Solo obtener si no está eliminado
        return author.map(this::convertToDTO);  // Convertir el Author a AuthorDTO si lo encuentra
    }

    @Override
    public AuthorDTO createAuthor(Long userId) { // userId es el ID del usuario en la tabla users
        UserSec user = userSecRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificamos si ya es AUTHOR
        boolean isAuthor = user.getRolesList().stream()
                .anyMatch(role -> role.getRole().equals("AUTHOR"));

        if (!isAuthor) {
            throw new RuntimeException("El usuario no tiene el rol de AUTHOR");
        }

        Author author = new Author();
        author.setUser(user);  // Asociamos el usuario al autor
        Author savedAuthor = authorRepository.save(author);

        return convertToDTO(savedAuthor);  // Devolvemos el Author como AuthorDTO
    }

    @Override
    public AuthorDTO updateAuthor(Long id, Author authorDetails) {
        // Buscar el Author por id
        Author existingAuthor = authorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        // Actualizar los detalles del Author
        existingAuthor.setUser(authorDetails.getUser());
        existingAuthor.setPosts(authorDetails.getPosts());

        // Guardar el Author actualizado
        Author updatedAuthor = authorRepository.save(existingAuthor);

        return convertToDTO(updatedAuthor);  // Devolvemos el Author actualizado como AuthorDTO
    }

    // Método para convertir Author a AuthorDTO
    private AuthorDTO convertToDTO(Author author) {
        List<PostDTO> postDTOs = author.getPosts().stream()
                .map(post -> new PostDTO(post.getId(), post.getTitle(), post.getContent()))
                .collect(Collectors.toList());

        return new AuthorDTO(
                author.getId(),
                author.getName(),
                postDTOs
        );
    }

    @Override
    public boolean deleteAuthor(Long id) {
        Optional<Author> author = authorRepository.findByIdAndDeletedFalse(id);
        if (author.isPresent()) {
            Author authorToDelete = author.get();
            authorToDelete.setDeleted(true);  // Marcamos el author como eliminado
            authorRepository.save(authorToDelete);
            return true;
        }
        return false;  // No encontrado o ya eliminado
    }


}
