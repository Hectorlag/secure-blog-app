package com.proyecto.blog.controller;

import com.proyecto.blog.dto.RoleRequestDTO;
import com.proyecto.blog.model.Permission;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.service.IPermissionService;
import com.proyecto.blog.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Tag(name = "Roles", description = "Operaciones para la gestión de roles del sistema")
@RestController
@RequestMapping("api/roles")
public class RoleController {

    private final IRoleService roleService;
    private final IPermissionService permissionService;

    @Autowired
    public RoleController(IRoleService roleService, IPermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Operation(
            summary = "Obtener todos los roles",
            description = "Retorna la lista de todos los roles del sistema",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Obtener un rol por ID",
            description = "Busca un rol por su ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear nuevo rol",
            description = "Crea un nuevo rol con una lista de permisos asociados",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        if (roleRequestDTO.getRole() == null || roleRequestDTO.getRole().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del rol es obligatorio");
        }

        if (roleRequestDTO.getPermissionIds() == null || roleRequestDTO.getPermissionIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Debe asignar al menos un permiso");
        }

        Set<Permission> permissions = new HashSet<>();

        for (Long permissionId : roleRequestDTO.getPermissionIds()) {
            Permission foundPermission = permissionService.findPermissionEntityById(permissionId).orElse(null);
            if (foundPermission != null) {
                permissions.add(foundPermission);
            }
        }

        Role role = new Role();
        role.setRole(roleRequestDTO.getRole());
        role.setPermissionsList(permissions);

        Role newRole = roleService.createRole(role);
        return ResponseEntity.ok(newRole);
    }

    @Operation(
            summary = "Actualizar un rol",
            description = "Modifica el nombre y los permisos de un rol existente",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        Role existingRole = roleService.getRoleById(id).orElse(null);
        if (existingRole == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Permission> permissions = new HashSet<>();

        for (Long permissionId : roleRequestDTO.getPermissionIds()) {
            Permission foundPermission = permissionService.findPermissionEntityById(permissionId).orElse(null);
            if (foundPermission != null) {
                permissions.add(foundPermission);
            }
        }

        existingRole.setRole(roleRequestDTO.getRole());
        existingRole.setPermissionsList(permissions);

        Role updatedRole = roleService.updateRole(id, existingRole);
        return ResponseEntity.ok(updatedRole);
    }

    @Operation(
            summary = "Eliminar un rol",
            description = "Elimina un rol por ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}

