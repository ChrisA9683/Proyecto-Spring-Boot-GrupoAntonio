
package pe.grupoantonio.gestion.demo.controller;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import pe.grupoantonio.gestion.demo.dto.CarritoDTO;
import pe.grupoantonio.gestion.demo.model.Carrito;
import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.repository.CarritoRepository;
import pe.grupoantonio.gestion.demo.repository.UsuarioRepository;

@RestController
@RequestMapping("/carritos") //Ruta para acceso a los carritos

public class CarritoController {
    
    private final CarritoRepository repo;
    private final UsuarioRepository usuarioRepo; 
    
    public CarritoController(CarritoRepository repo, UsuarioRepository usuarioRepo){
    this.repo = repo;
    this.usuarioRepo=usuarioRepo;
    }
    
        // 📌 GET: Listar todos los carritos
    @GetMapping
    public List<Carrito> listar() {
        return repo.findAll();
    }

    // 📌 GET: Buscar un carrito por ID
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> buscarPorId(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok) // Si lo encuentra, devuelve 200 OK + carrito
                .orElse(ResponseEntity.notFound().build()); // Si no, 404 Not Found
    }

     // 📌 POST: Crear un carrito nuevo
    @PostMapping
    public ResponseEntity<CarritoDTO> crear(@RequestBody CarritoDTO dto) {
    Usuario usuario = usuarioRepo.findById(dto.getUsuarioId()).orElse(null);
    if (usuario == null) return ResponseEntity.badRequest().build();

    Carrito carrito = new Carrito();
    carrito.setEstado(dto.getEstado());
    carrito.setUsuario(usuario);
    carrito.setFechaCreacion(LocalDateTime.now());

    Carrito guardado = repo.save(carrito);

    // Devolver DTO limpio
    CarritoDTO resp = new CarritoDTO();
    resp.setEstado(guardado.getEstado());
    resp.setFechaCreacion(guardado.getFechaCreacion());
    resp.setUsuarioId(guardado.getUsuario().getId());
    return ResponseEntity.ok(resp);
    }

    // 📌 PUT: Actualizar estado de un carrito
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizar(@PathVariable Long id, @RequestBody CarritoDTO carrito) {
        return repo.findById(id).map(c -> {
            // Solo actualizamos el estado (activo, abandonado, convertido)
            c.setEstado(carrito.getEstado());
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 📌 DELETE: Eliminar un carrito
    @DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    Optional<Carrito> carritoOpt = repo.findById(id);
    if (carritoOpt.isPresent()) {
        repo.delete(carritoOpt.get());
        return ResponseEntity.noContent().build(); // 204 No Content
    } else {
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
}
    
}
