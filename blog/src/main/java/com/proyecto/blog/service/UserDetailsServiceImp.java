package com.proyecto.blog.service;

import com.proyecto.blog.dto.AuthLoginRequestDTO;
import com.proyecto.blog.dto.AuthResponseDTO;
import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IUserSecRepository;
import com.proyecto.blog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private IUserSecRepository userRepo;

    @Autowired
    private JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImp(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Obtengo un UserSec y debo devolverlo en formato UserDetails
        // Obtengo al usuario de nuestra BD
        UserSec userSec = userRepo.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no fue encontrado"));

        // Creo una lista para los permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Obtengo los roles y los convierto en SimpleGrantedAuthority
        userSec.getRolesList().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole()))));

        // Agrego los permisos de los roles
        userSec.getRolesList().stream()
                .flatMap(role -> role.getPermissionsList().stream())  // Recorro los permisos de los roles
                .forEach(permission ->
                        authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        System.out.println("Authorities: " + authorityList);
        System.out.println("Comparando contra el hash:");
        System.out.println(passwordEncoder.matches("admin123", "$2a$10$Dow1U3jE5IYiLxqrIBuEQuyNdKJeMVpE5Vf9BoGvJWDsmFY.gK/zu"));


        // Retorno el usuario en formato Spring Security con los datos de nuestro userSec
        return new User(userSec.getUsername(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNotExpired(),
                userSec.isCredentialNotExpired(),
                userSec.isAccountNotLocked(),
                authorityList);
    }

    public AuthResponseDTO loginUser(AuthLoginRequestDTO authLoginRequest) {
        // Recupero el nombre de usuario y contraseña
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);

        // Si todo está ok, se guarda la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genero el token JWT
        String accessToken = jwtUtils.createToken(authentication);

        // Retorno la respuesta de autenticación
        return new AuthResponseDTO(username, "Login OK", accessToken, true);
    }

    public Authentication authenticate(String username, String password) {
        // Busco el usuario
        UserDetails userDetails = this.loadUserByUsername(username);

        // Verifico si el usuario no existe
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Verifico si la contraseña es correcta
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Retorno la autenticación
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    // Nuevo método para crear o actualizar un usuario (puede ser útil)
    public UserSec createOrUpdateUser(String username, String name) {
        UserSec userSec = userRepo.findUserEntityByUsername(username).orElse(null);

        if (userSec == null) {
            // Si el usuario no existe, lo creamos
            userSec = new UserSec();
            userSec.setUsername(username);
            userSec.setPassword(passwordEncoder.encode("defaultPassword"));  // Definir una contraseña temporal

            // Agregar un rol por defecto
            Role role = new Role();
            role.setRole("USER");
            userSec.setRolesList(new HashSet<>(Collections.singletonList(role)));

            userRepo.save(userSec);
        }

        return userSec;
    }
}

