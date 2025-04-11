package com.proyecto.blog.service;


import com.proyecto.blog.dto.PermissionDTO;
import com.proyecto.blog.dto.PermissionResponseDTO;
import com.proyecto.blog.model.Permission;

import java.util.List;
import java.util.Optional;

public interface IPermissionService {
        PermissionResponseDTO createPermission(PermissionDTO permissionDTO);
        PermissionResponseDTO getPermissionById(Long id);
        Optional<Permission> findPermissionEntityById(Long id);
        List<PermissionResponseDTO> getAllPermissions();
        PermissionResponseDTO updatePermission(Long id, PermissionDTO permissionDTO);
        void deletePermission(Long id);

}
