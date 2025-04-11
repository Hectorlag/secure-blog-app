package com.proyecto.blog.excepcion;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
        super(message); // Pasamos el mensaje al constructor de RuntimeException
    }
}
