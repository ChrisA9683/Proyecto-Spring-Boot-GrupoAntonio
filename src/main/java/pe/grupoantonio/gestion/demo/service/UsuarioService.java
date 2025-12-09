package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.repository.UsuarioRepository;
import pe.grupoantonio.gestion.demo.model.Rol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
// Implementamos la interfaz de seguridad y la interfaz de negocio
public class UsuarioService implements UserDetailsService, IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class); 

    @Autowired
    private UsuarioRepository repo;
    
    // ... (Inyecciones comentadas) ...

    // =======================================================
    // MÉTODOS DE SPRING SECURITY (Implementación de UserDetailsService)
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Manejo seguro del rol
        String nombreRol = "USER"; // Rol por defecto
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            nombreRol = usuario.getRol().getNombre(); 
        }

        // Spring Security requiere el prefijo "ROLE_"
        String roleAuthority = "ROLE_" + nombreRol.toUpperCase();

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleAuthority))
        );
    }

    // =======================================================
    // MÉTODOS DE NEGOCIO (Implementación de IUsuarioService)
    // =======================================================

    @Override
    @Transactional
    public void registrarUsuario(Usuario usuario) {
        
        // --- 1. NUEVO REGISTRO (ID es nulo) ---
        if (usuario.getId() == null) {
            repo.save(usuario);
        } else {
            // --- 2. EDICIÓN (ID existe) ---
            Optional<Usuario> usuarioOpt = repo.findById(usuario.getId());

            usuarioOpt.ifPresent(usuarioExistente -> {
                
                // 2.1 Actualizar Campos Simples (Nombre y Email)
                usuarioExistente.setNombre(usuario.getNombre());
                usuarioExistente.setEmail(usuario.getEmail());
                
                // 2.2 Actualizar Rol
                if (usuario.getRol() != null) {
                    usuarioExistente.setRol(usuario.getRol());
                }

                // 2.3 Actualizar Contraseña SOLO si se proporcionó una nueva
                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    usuarioExistente.setPassword(usuario.getPassword()); 
                }
                
                repo.save(usuarioExistente);
            });
        }
    }
    
    /**
     * @return Lista de todos los usuarios.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return repo.findAll();
    }
    
    /**
     * @param email
     * @return Busca un usuario por email o null si no existe.
     * ❌ CORRECCIÓN: Eliminamos el try-catch de bajo nivel para que los errores de DB se propaguen.
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        // Si hay un error de DB/JPA, se lanzará una RuntimeException, que es lo que queremos.
        // Si no lo encuentra, devuelve Optional.empty(), y .orElse(null) devuelve null, 
        // lo que el CarritoController interpreta como 404.
        return repo.findByEmail(email).orElse(null);
    }

    /**
     * @param id
     * @return Busca un usuario por ID o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return repo.findById(id).orElse(null);  
    }
}