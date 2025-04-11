package com.proyecto.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSecResponseDTO {

    private String username;
    private Set<String> rolesList;
    @JsonInclude(JsonInclude.Include.NON_NULL)  // Evita que aparezca si es null
    private Long authorId; // Si el usuario es autor, devuelve solo su ID

    // Método estático para convertir de UserSec a UserSecResponseDTO
    public static UserSecResponseDTO fromUserSec(UserSec user) {
        return new UserSecResponseDTO(
                user.getUsername(),
                user.getRolesList().stream().map(Role::getRole).collect(Collectors.toSet()), // ← Usa rolesList
                user.getAuthor() != null ? user.getAuthor().getId() : null
        );
    }
}
