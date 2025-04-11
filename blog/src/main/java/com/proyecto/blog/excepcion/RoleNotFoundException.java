package com.proyecto.blog.excepcion;

public class RoleNotFoundException extends RuntimeException{

    public RoleNotFoundException(String message) {
        super(message); // Pasamos el mensaje al constructor de RuntimeException
    }
}
