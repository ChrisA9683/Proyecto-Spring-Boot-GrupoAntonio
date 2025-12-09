package pe.grupoantonio.gestion.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import pe.grupoantonio.gestion.demo.model.Cotizacion;
import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.repository.CotizacionRepository;
import pe.grupoantonio.gestion.demo.repository.UsuarioRepository;

@RestController
@RequestMapping("/cotizaciones")
public class CotizacionController {

    private final CotizacionRepository repo;
    private final UsuarioRepository usuarioRepo;

    public CotizacionController(CotizacionRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
    }

    // GET: listar todas
    @GetMapping
    public List<Cotizacion> listar() {
        return repo.findAll();
    }

    // GET: buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> buscar(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear cotización
    @PostMapping
    public ResponseEntity<Cotizacion> crear(@RequestParam Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.badRequest().build();

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setUsuario(usuario);
        cotizacion.setFecha(LocalDateTime.now());
        cotizacion.setEstado("SOLICITADA");
        cotizacion.setTotal(0.0);

        return ResponseEntity.ok(repo.save(cotizacion));
    }

    // PUT: actualizar estado
    @PutMapping("/{id}")
    public ResponseEntity<Cotizacion> actualizar(@PathVariable Long id, @RequestParam String estado) {
        return repo.findById(id).map(c -> {
            c.setEstado(estado);
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE: eliminar cotización
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return repo.findById(id).map(c -> {
            repo.delete(c);
            return ResponseEntity.noContent().<Void>build(); 
            })
            .orElse(ResponseEntity.notFound().build());

    }
}
