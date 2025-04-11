package com.proyecto.blog.repository;

import com.proyecto.blog.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByDeletedFalse();  // Método para obtener permissions no eliminados

    Optional<Permission> findByIdAndDeletedFalse(Long id);  // Método para obtener un post por ID no eliminado
}
