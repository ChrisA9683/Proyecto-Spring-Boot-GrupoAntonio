package pe.grupoantonio.gestion.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

import pe.grupoantonio.gestion.demo.model.DetalleCarrito;
import pe.grupoantonio.gestion.demo.model.Usuario;
import pe.grupoantonio.gestion.demo.service.ICarritoService;
import pe.grupoantonio.gestion.demo.service.IUsuarioService;
import pe.grupoantonio.gestion.demo.exception.RecursoNoEncontradoException; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);
    @Autowired private ICarritoService carritoService;
    @Autowired private IUsuarioService usuarioService;

    private Long getUsuarioId(UserDetails principal) {
        if (principal == null) throw new RuntimeException("Usuario no autenticado."); 
        try { 
            Usuario usuario = usuarioService.buscarPorEmail(principal.getUsername());
            if (usuario == null || usuario.getId() == null) {
                throw new RecursoNoEncontradoException("Usuario no encontrado en DB.");
            }
            return usuario.getId();
        } catch (RecursoNoEncontradoException e) { throw e; } 
        catch (Exception e) {
            // ✅ FORZAR IMPRESIÓN DE ERROR EN CONSOLA
            e.printStackTrace(); 
            logger.error("Fallo crítico DB al buscar usuario: {}", e.getMessage());
            throw new RuntimeException("Error interno al validar usuario: " + e.getMessage(), e);
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado."));
        
        try {
            Long usuarioId = getUsuarioId(principal); 
            Long productoId = Long.valueOf(payload.get("productoId").toString());
            Integer cantidad = Integer.valueOf(payload.get("cantidad").toString());
            
            DetalleCarrito detalle = carritoService.agregarProducto(usuarioId, productoId, cantidad);
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "mensaje", "Producto añadido.", 
                "nuevaCantidad", detalle.getCantidad()
            ));
        } catch (RecursoNoEncontradoException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", e.getMessage()));
        } catch (RuntimeException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            // ✅ FORZAR IMPRESIÓN DE ERROR EN CONSOLA
            e.printStackTrace();
            logger.error("Error 500 al agregar: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerCarrito(@AuthenticationPrincipal UserDetails principal) {
         if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado."));
         try { 
             return ResponseEntity.ok(carritoService.obtenerDetallesDelCarrito(getUsuarioId(principal))); 
         } catch (Exception e) { 
             e.printStackTrace();
             return ResponseEntity.status(500).body(Map.of("error", e.getMessage())); 
         }
    }

    @PutMapping("/actualizar/{detalleId}")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Long detalleId, @RequestBody Map<String, Integer> payload, @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado."));
        try {
            DetalleCarrito detalle = carritoService.actualizarCantidad(getUsuarioId(principal), detalleId, payload.get("cantidad"));
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Actualizado.", "nuevaCantidad", detalle.getCantidad()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{detalleId}")
    public ResponseEntity<?> eliminarItem(@PathVariable Long detalleId, @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado."));
        try {
            carritoService.eliminarDetalle(getUsuarioId(principal), detalleId);
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Eliminado."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}