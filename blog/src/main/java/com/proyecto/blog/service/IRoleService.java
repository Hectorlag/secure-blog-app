package com.proyecto.blog.service;

import com.proyecto.blog.model.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

        Role createRole(Role role); // Crear un nuevo Role
        Optional<Role> getRoleById(Long id); // Obtener Role por id
        List<Role> getAllRoles(); // Obtener todos los Roles
        Role updateRole(Long id, Role roleDetails); // Actualizar un Role
        boolean deleteRole(Long id); // Eliminar un Role

}
