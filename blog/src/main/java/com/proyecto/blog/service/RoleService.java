package com.proyecto.blog.service;

import com.proyecto.blog.model.Role;
import com.proyecto.blog.repository.IRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements IRoleService{

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findByDeletedFalse();
    }

    @Override
    public Role updateRole(Long id, Role roleDetails) {
        return roleRepository.findByIdAndDeletedFalse(id).map(role -> {
            role.setRole(roleDetails.getRole()); // Actualiza el nombre del rol
            role.setPermissionsList(roleDetails.getPermissionsList()); // Actualiza los permisos del rol
            return roleRepository.save(role);
        }).orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Override
    public boolean deleteRole(Long id) {

        Role role = roleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setDeleted(true); // Marcamos el role como eliminado
        roleRepository.save(role); // Guardamos el cambio en la base de datos

        return true; // Indicamos que la operación fue exitosa
    }

    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRole(roleName);
    }

}
