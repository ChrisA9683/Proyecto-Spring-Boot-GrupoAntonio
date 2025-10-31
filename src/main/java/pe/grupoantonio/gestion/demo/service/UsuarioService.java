package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;


import java.util.Collections;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repo;

    // Método que Spring Security usa para autenticar al usuario
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Busca al usuario por su correo en la base de datos
        Usuario usuario = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Asegura que el rol tenga el prefijo ROLE_
        String role = usuario.getRol();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        // Retorna el usuario autenticado con su correo, contraseña y rol
        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()))
        );
    }

    // Puedes agregar aquí otros métodos que usen tus controladores, como registrar o listar usuarios
    public void registrarUsuario(Usuario usuario) {
        repo.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }
}