package com.proyecto.blog.dto;

import com.proyecto.blog.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionResponseDTO {

    private Long id;
    private String permissionName;


    public static PermissionResponseDTO fromEntity(Permission permission) {
        return new PermissionResponseDTO(permission.getId(), permission.getPermissionName());
    }
}
