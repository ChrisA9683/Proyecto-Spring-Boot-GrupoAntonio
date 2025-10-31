package pe.grupoantonio.gestion.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import pe.grupoantonio.gestion.demo.model.Pedido;
import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.repository.PedidoRepository;
import pe.grupoantonio.gestion.demo.repository.UsuarioRepository;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository repo;
    private final UsuarioRepository usuarioRepo;

    public PedidoController(PedidoRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
    }

    // GET: listar todos
    @GetMapping
    public List<Pedido> listar() {
        return repo.findAll();
    }

    // GET: buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear pedido
    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestParam Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.badRequest().build();

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(0.0);

        return ResponseEntity.ok(repo.save(pedido));
    }

    // PUT: actualizar estado
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Long id, @RequestParam String estado) {
        return repo.findById(id).map(p -> {
            p.setEstado(estado);
            return ResponseEntity.ok(repo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE: eliminar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return repo.findById(id).map(p -> {
            repo.delete(p);
            return ResponseEntity.noContent().<Void>build(); // 👈 diferencia
    }).orElse(ResponseEntity.<Void>notFound().build());   // 👈 diferencia
    }
}
