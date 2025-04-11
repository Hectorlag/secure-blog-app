package com.proyecto.blog.service;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.excepcion.RoleNotFoundException;
import com.proyecto.blog.excepcion.UserNotFoundException;
import com.proyecto.blog.model.Author;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IAuthorRepository;
import com.proyecto.blog.repository.IRoleRepository;
import com.proyecto.blog.repository.IUserSecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserSecService {

    @Autowired
    private IUserSecRepository userSecRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IAuthorRepository iAuthorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyección del BCryptPasswordEncoder

    public UserSec registerUser(UserDTO userDTO, boolean isAuthor, String authorName, boolean isAdminRequest) {

        // Si el registro NO es hecho por un ADMIN, restringimos los roles permitidos
        if (!isAdminRequest) {
            userDTO.getRoles().removeIf(role -> role.equalsIgnoreCase("ADMIN"));
            if (userDTO.getRoles().isEmpty()) {
                userDTO.getRoles().add("USER"); // Si no especifica roles, asignamos USER por defecto
            }
        }

        // Crear el usuario
        UserSec user = new UserSec();
        user.setUsername(userDTO.getUsername());
        user.setPassword(this.encriptPassword(userDTO.getPassword())); // Encriptar la contraseña
        user.setEnabled(true);
        user.setAccountNotLocked(true);
        user.setAccountNotExpired(true);
        user.setCredentialNotExpired(true);

        // Asignar roles según los roles del DTO
        Set<Role> roles = new HashSet<>();
        for (String roleName : userDTO.getRoles()) {
            Role role = roleRepository.findByRole(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Rol not found: " + roleName));
            roles.add(role);
        }
        user.setRolesList(roles);

        // Guardar usuario
        UserSec savedUser = userSecRepository.save(user);

        // Si es autor, creamos automáticamente el Author con su nombre
        if (isAuthor) {
            Author author = new Author();
            author.setUser(savedUser);
            author.setName(authorName);  // Guardamos el nombre del autor
            iAuthorRepository.save(author);
            savedUser.setAuthor(author); // Establecer la relación en el UserSec
        }

        return savedUser;
    }


    @Override
    public Optional<UserSec> getUserSecById(Long id) {
        return userSecRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<UserSec> getAllUserSecs() {
        return userSecRepository.findByDeletedFalse();
    }

    @Override
    public UserSec updateUserSec(Long id, UserDTO userDTO, boolean isAuthor, String authorName) {
        UserSec existingUser = userSecRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Actualizar solo los campos que se envían
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(this.encriptPassword(userDTO.getPassword()));
        }

        // Asignar roles desde el DTO si vienen
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<Role> roles = userDTO.getRoles().stream()
                    .map(roleName -> roleRepository.findByRole(roleName)
                            .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado: " + roleName)))
                    .collect(Collectors.toSet());

            existingUser.setRolesList(roles);
        }

        // Gestionar la relación con Author
        if (isAuthor) {
            if (existingUser.getAuthor() == null) {
                if (authorName == null || authorName.isBlank()) {
                    throw new IllegalArgumentException("El nombre del autor es obligatorio");
                }

                Author author = new Author();
                author.setUser(existingUser);
                author.setName(authorName);
                iAuthorRepository.save(author);
                existingUser.setAuthor(author);
            } else {
                // Si ya tiene un author, actualizamos el nombre si se envió
                if (authorName != null && !authorName.isBlank()) {
                    existingUser.getAuthor().setName(authorName);
                }
            }
        } else {
            existingUser.setAuthor(null); // Si ya no es autor, eliminamos la relación
        }

        return userSecRepository.save(existingUser);
    }


    @Override
    public boolean deleteUserSec(Long id) {
        UserSec userSec = userSecRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("UserSec not found with id: " + id));

        userSec.setDeleted(true);
        userSecRepository.save(userSec);

        return true;
    }

    @Override
    public String encriptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}

