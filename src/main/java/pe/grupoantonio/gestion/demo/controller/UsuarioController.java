package pe.grupoantonio.gestion.demo.controller;

import java.security.Principal;
import pe.grupoantonio.gestion.demo.dto.UsuarioDTO;
import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.grupoantonio.gestion.demo.service.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;
     @Autowired
    private UsuarioService usuarioService;

    // Página de inicio (index)
    @GetMapping("/")
    public String index() {
        return "index";  // Correspondiente al archivo index.html
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "login";  // Correspondiente al archivo login.html
    }
     // Procesar el login
    @PostMapping("/login")
    public String validarLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model) {

        Usuario usuario = repo.findByEmail(email)
    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario == null) {
            model.addAttribute("error", "El correo no está registrado.");
            return "login";
        }

        // Comparar contraseña cifrada
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "login";
        }

        // Si todo es correcto, redirigir al dashboard
        return "dashboard/inicio-dashboard";
    }


    // Página de registro
    @GetMapping("/registro")
    public String registro() {
        return "registro";  // Correspondiente al archivo registro.html
    }

    @PostMapping("/registro")
    public String crear(@ModelAttribute UsuarioDTO usuarioDTO, Model model) {
        if (!usuarioDTO.getPassword().equals(usuarioDTO.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }
        if (usuarioDTO.getNombre().isEmpty() || usuarioDTO.getEmail().isEmpty() || usuarioDTO.getPassword().isEmpty()) {
            model.addAttribute("error", "Todos los campos son obligatorios.");
            return "registro";
        }
        if (repo.existsByEmail(usuarioDTO.getEmail())) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            return "registro";
        }
        String encryptedPassword = passwordEncoder.encode(usuarioDTO.getPassword());
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(encryptedPassword);
        usuario.setRol("USER");
        repo.save(usuario);
        return "redirect:/usuario/login";
    }

    

    // Otras páginas públicas
    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";  // Correspondiente al archivo carrito.html
    }

    @GetMapping("/catalogo")
    public String catalogo() {
        return "catalogo";  // Correspondiente al archivo catalogo.html
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";  // Correspondiente al archivo contacto.html
    }

    @GetMapping("/resultado")
    public String resultado() {
        return "resultado";  // Correspondiente al archivo resultado.html
    }

    @GetMapping("/servicios")
    public String servicios() {
        return "servicios";  // Correspondiente al archivo servicios.html
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Obtiene el correo del usuario logueado desde Spring Security
        String email = principal.getName();

        // Trae el usuario completo desde la base de datos
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario != null) {
            model.addAttribute("nombreUsuario", usuario.getNombre());
        }
         return "dashboard/inicio-dashboard";
    }
}   