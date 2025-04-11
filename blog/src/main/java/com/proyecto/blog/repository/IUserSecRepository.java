package com.proyecto.blog.repository;

import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserSecRepository extends JpaRepository<UserSec, Long> {

    Optional<UserSec> findUserEntityByUsername(String username);

    List<UserSec> findByDeletedFalse();  // Método para obtener UserSec no eliminados

    Optional<UserSec> findByIdAndDeletedFalse(Long id);  // Método para obtener un UserSec por ID no eliminado

    Optional<UserSec> findByUsername(String username);


}
