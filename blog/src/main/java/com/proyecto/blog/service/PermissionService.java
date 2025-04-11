package com.proyecto.blog.service;

import com.proyecto.blog.dto.PermissionDTO;
import com.proyecto.blog.dto.PermissionResponseDTO;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Permission;
import com.proyecto.blog.repository.IPermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService implements IPermissionService{

    @Autowired
    private IPermissionRepository permissionRepository;

    @Override
    public PermissionResponseDTO createPermission(PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        permission.setPermissionName(permissionDTO.getPermissionName());

        Permission savedPermission = permissionRepository.save(permission);
        return PermissionResponseDTO.fromEntity(savedPermission);
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        return PermissionResponseDTO.fromEntity(permission);
    }

    @Override
    public Optional<Permission> findPermissionEntityById(Long id) {
        return permissionRepository.findByIdAndDeletedFalse(id);
    }

        @Override
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findByDeletedFalse()
                .stream()
                .map(PermissionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        permission.setPermissionName(permissionDTO.getPermissionName());
        Permission updatedPermission = permissionRepository.save(permission);

        return PermissionResponseDTO.fromEntity(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        permission.setDeleted(true);
        permissionRepository.save(permission);
    }



}

