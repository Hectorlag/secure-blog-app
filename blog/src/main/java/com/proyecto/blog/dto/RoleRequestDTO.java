package com.proyecto.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleRequestDTO {
        @NotBlank(message = "El nombre del rol es obligatorio")
        private String role;

        @NotEmpty(message = "Debe asignar al menos un permiso")
        private Set<Long> permissionIds = new HashSet<>(); // Inicializado como un Set vacío

}
