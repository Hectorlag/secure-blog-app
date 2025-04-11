package com.proyecto.blog.repository;

import com.proyecto.blog.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByDeletedFalse();  // Método para obtener roles no eliminados

    Optional<Role> findByIdAndDeletedFalse(Long id);  // Método para obtener un role por ID no eliminado

    Optional<Role> findByRole(String roleName);

    @Query("SELECT r FROM Role r WHERE r.role = :role")
    Optional<Role> findByRoleName(@Param("role") String role);


}
