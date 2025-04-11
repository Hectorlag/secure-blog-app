package com.proyecto.blog.controller;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.dto.UserSecResponseDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.service.IRoleService;
import com.proyecto.blog.service.IUserSecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private IUserSecService userService;

    @Operation(
            summary = "Obtener información del usuario autenticado",
            description = "Devuelve el username y los roles del usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay usuario autenticado.");
        }
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", authentication.getName());
        userInfo.put("roles", authentication.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }

    @Operation(
            summary = "Verificar acceso ADMIN",
            description = "Confirma que el usuario autenticado tenga rol ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<String> getAdminStatus() {
        return ResponseEntity.ok("🔐 Acceso concedido: Estás autenticado como ADMIN y tienes acceso a los recursos protegidos.");
    }

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Solo accesible por ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserSecResponseDTO>> getAllUsers() {
        List<UserSecResponseDTO> userList = userService.getAllUserSecs().stream()
                .map(UserSecResponseDTO::fromUserSec)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Solo accesible por ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserSecResponseDTO> getUserById(@PathVariable Long id) {
        UserSec user = userService.getUserSecById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(user));
    }

    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario desde el panel de ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserSecResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserSec newUser = userService.registerUser(userDTO, userDTO.isAuthor(), userDTO.getAuthorName(), true);
        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(newUser));
    }

    @Operation(
            summary = "Registro de usuario",
            description = "Permite a cualquier usuario registrarse como USER o AUTHOR"
    )
    @PostMapping("/register")
    public ResponseEntity<UserSecResponseDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserSec newUser = userService.registerUser(userDTO, userDTO.isAuthor(), userDTO.getAuthorName(), false);
        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(newUser));
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Permite a un ADMIN modificar un usuario existente",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<UserSecResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO,
            @RequestParam(required = false, defaultValue = "false") boolean isAuthor,
            @RequestParam(required = false) String authorName) {

        UserSec updatedUser = userService.updateUserSec(id, userDTO, isAuthor, authorName);
        return ResponseEntity.ok(UserSecResponseDTO.fromUserSec(updatedUser));
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario por ID (solo ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserSec(id);
        return ResponseEntity.noContent().build();
    }

    // Método de utilidad (no se expone en Swagger)
    public static UserSecResponseDTO fromUserSec(UserSec user) {
        return new UserSecResponseDTO(
                user.getUsername(),
                user.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()),
                user.getAuthor() != null ? user.getAuthor().getId() : null
        );
    }
}