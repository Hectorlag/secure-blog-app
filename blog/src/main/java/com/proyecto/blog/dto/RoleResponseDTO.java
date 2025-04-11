package com.proyecto.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RoleResponseDTO {

        private Long id;
        private String role;
        private Set<PermissionDTO> permissions;

}
