package com.proyecto.blog.controller;


import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.service.IAuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Author", description = "Operaciones para la gestión de autores del sistema")
@RestController
@RequestMapping("api/authors")
public class AuthorController {

    @Autowired
    private IAuthorService authorService;

    @Operation(
            summary = "Obtener todos los autores",
            description = "Devuelve la lista de todos los autores del sistema. Accesible por ADMIN, USER y AUTHOR",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @Operation(
            summary = "Obtener autor por ID",
            description = "Devuelve un autor específico según su ID. Accesible por ADMIN, USER y AUTHOR",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Optional<AuthorDTO> author = authorService.getAuthorById(id);
        return author.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear un autor",
            description = "Crea un autor a partir de un usuario existente. Solo accesible por ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}")
    public ResponseEntity<AuthorDTO> createAuthor(@PathVariable Long userId) {
        AuthorDTO newAuthor = authorService.createAuthor(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAuthor);
    }

    @Operation(
            summary = "Actualizar autor",
            description = "Permite a un ADMIN modificar la información de un autor",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
        AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDetails);
        if (updatedAuthor != null) {
            return ResponseEntity.ok(updatedAuthor);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Eliminar autor",
            description = "Permite a un ADMIN eliminar un autor por ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Verificar acceso de autor",
            description = "Muestra el rol con el que el usuario está autenticado. Accesible por ADMIN, USER y AUTHOR",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping("/status")
    public ResponseEntity<String> getAuthorAccessStatus(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(roleName -> roleName.replace("ROLE_", ""))
                .findFirst()
                .orElse("Sin rol asignado");

        return ResponseEntity.ok("✅ Estás autenticado como: " + role);
    }
}

