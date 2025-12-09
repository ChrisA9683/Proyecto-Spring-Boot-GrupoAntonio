package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.model.Usuario;
import java.util.List;

public interface IUsuarioService {
    
    // MÉTODO REQUERIDO PARA TU VISTA DE ADMINISTRACIÓN
    List<Usuario> obtenerTodosLosUsuarios();
    
    // Aquí van los demás métodos que no son de Spring Security (registrarUsuario, buscarPorEmail, findById)
    Usuario buscarPorEmail(String email);
    void registrarUsuario(Usuario usuario);
    Usuario findById(Long id);
}