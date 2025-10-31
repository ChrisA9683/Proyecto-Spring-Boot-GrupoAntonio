
package pe.grupoantonio.gestion.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pe.grupoantonio.gestion.demo.model.Carrito;
import pe.grupoantonio.gestion.demo.model.DetalleCarrito;
import pe.grupoantonio.gestion.demo.model.Producto;
import pe.grupoantonio.gestion.demo.repository.DetalleCarritoRepository;
import pe.grupoantonio.gestion.demo.repository.CarritoRepository;
import pe.grupoantonio.gestion.demo.repository.ProductoRepository;
import pe.grupoantonio.gestion.demo.dto.DetalleCarritoDTO;

@RestController
@RequestMapping ("/detalles-carrito")// 📌 Ruta para gestionar los detalles

public class DetalleCarritoController {
    
    private final DetalleCarritoRepository repo;
    private final CarritoRepository carritoRepo;
    private final ProductoRepository productoRepo;
    
    public DetalleCarritoController(DetalleCarritoRepository repo,
            CarritoRepository carritoRepo,
            ProductoRepository productoRepo){
  
        this.repo=repo;
        this.carritoRepo=carritoRepo;
        this.productoRepo=productoRepo;
    }
    
      // 📌 GET: Listar todos los detalles de carrito
    @GetMapping
    public List<DetalleCarritoDTO> listar() {
        return repo.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    // 📌 GET: Buscar un detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleCarritoDTO> buscarPorId(@PathVariable Long id) {
        return repo.findById(id)
                .map(det -> ResponseEntity.ok(convertirADTO(det)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 📌 POST: Agregar un detalle al carrito
    @PostMapping
    public ResponseEntity<DetalleCarritoDTO> crear(@RequestBody DetalleCarritoDTO dto) {
        Carrito carrito = carritoRepo.findById(dto.getCarritoId()).orElse(null);
        Producto producto = productoRepo.findById(dto.getProductoId()).orElse(null);

        if (carrito == null || producto == null) {
            return ResponseEntity.badRequest().build(); // Si no existe carrito o producto
        }

        DetalleCarrito detalle = new DetalleCarrito();
        detalle.setCarrito(carrito);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());

        DetalleCarrito guardado = repo.save(detalle);
        return ResponseEntity.ok(convertirADTO(guardado));
    }

    // 📌 PUT: Actualizar cantidad de un producto en el carrito
    @PutMapping("/{id}")
    public ResponseEntity<DetalleCarritoDTO> actualizar(@PathVariable Long id, @RequestBody DetalleCarritoDTO dto) {
        return repo.findById(id).map(det -> {
            det.setCantidad(dto.getCantidad());
            det.setPrecioUnitario(dto.getPrecioUnitario());
            DetalleCarrito actualizado = repo.save(det);
            return ResponseEntity.ok(convertirADTO(actualizado));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    Optional<DetalleCarrito> detalleOpt = repo.findById(id);

    if (detalleOpt.isPresent()) {
        repo.delete(detalleOpt.get());
        return ResponseEntity.noContent().build(); // 204
    } else {
        return ResponseEntity.notFound().build(); // 404
    }
}

    // ✅ Conversión Entidad → DTO
    private DetalleCarritoDTO convertirADTO(DetalleCarrito detalle) {
        DetalleCarritoDTO dto = new DetalleCarritoDTO();
        dto.setId(detalle.getId());
        dto.setCarritoId(detalle.getCarrito().getId());
        dto.setProductoId(detalle.getProducto().getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }
    
    
    
}
