package com.proyecto.blog.repository;

import com.proyecto.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {

    List<Post> findByDeletedFalse();  // Método para obtener posts no eliminados

    Optional<Post> findByIdAndDeletedFalse(Long id);  // Método para obtener un post por ID no eliminado
}
