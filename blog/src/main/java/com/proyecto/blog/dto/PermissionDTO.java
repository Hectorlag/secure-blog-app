package com.proyecto.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PermissionDTO {

    @NotBlank(message = "El nombre del permiso no puede estar vacío")
    private String permissionName;
}
