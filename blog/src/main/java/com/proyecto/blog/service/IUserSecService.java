package com.proyecto.blog.service;

import com.proyecto.blog.dto.UserDTO;
import com.proyecto.blog.model.UserSec;

import java.util.List;
import java.util.Optional;

public interface IUserSecService {

    // Método para registrar un nuevo usuario (común o autor)
    UserSec registerUser(UserDTO userDTO, boolean isAuthor, String authorName, boolean isAdminRequest); // Crear un nuevo UserSec
    Optional<UserSec> getUserSecById(Long id); // Obtener UserSec por id
    List<UserSec> getAllUserSecs(); // Obtener todos los UserSecs
    UserSec updateUserSec(Long id, UserDTO userDTO, boolean isAuthor, String authorName); // Actualizar un UserSec
    boolean deleteUserSec(Long id); // Eliminar un UserSec
    //agregamos el método de encriptado
    public String encriptPassword(String password);
}
