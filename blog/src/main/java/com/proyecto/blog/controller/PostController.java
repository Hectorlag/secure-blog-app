package com.proyecto.blog.controller;

import com.proyecto.blog.dto.AuthorDTO;
import com.proyecto.blog.dto.PostDTOandNameAuthor;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Post;
import com.proyecto.blog.service.IAuthorService;
import com.proyecto.blog.service.IPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Tag(name = "Post", description = "Operaciones para la gestión de posts del sistema")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IAuthorService authorService;

    @Operation(
            summary = "Obtener todos los posts",
            description = "Accesible por ADMIN, USER o AUTHOR",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping
    public ResponseEntity<List<PostDTOandNameAuthor>> getAllPosts() {
        List<PostDTOandNameAuthor> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @Operation(
            summary = "Obtener un post por ID",
            description = "Devuelve un post según su ID. Accesible por ADMIN, USER o AUTHOR",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUTHOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PostDTOandNameAuthor> getPostById(@PathVariable Long id) {
        Optional<PostDTOandNameAuthor> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear un nuevo post",
            description = "Crea un post. Accesible por ADMIN o AUTHOR",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @PostMapping
    public ResponseEntity<PostDTOandNameAuthor> createPost(@RequestBody Post post) {
        PostDTOandNameAuthor newPostDTO = postService.createPost(post);
        return ResponseEntity.ok(newPostDTO);
    }

    @Operation(
            summary = "Actualizar un post",
            description = "Permite a un AUTHOR o ADMIN actualizar un post existente",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<PostDTOandNameAuthor> updatePost(
            @PathVariable Long id,
            @RequestBody Post postDetails,
            Authentication authentication) {

        PostDTOandNameAuthor updatedPost = postService.updatePost(id, postDetails, authentication);
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(
            summary = "Eliminar un post",
            description = "Elimina un post por ID. Solo ADMIN puede realizar esta operación",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener estado del usuario autenticado",
            description = "Devuelve el nombre de usuario y sus roles actuales",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'AUTHOR')")
    @GetMapping("/status")
    public ResponseEntity<String> getUserStatus(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = authorities.stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String responseMessage = String.format("✅ Usuario: %s\nRoles: %s", username, roles.toString());
        return ResponseEntity.ok(responseMessage);
    }
}

