package pe.grupoantonio.gestion.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.grupoantonio.gestion.demo.model.*;
import pe.grupoantonio.gestion.demo.dto.CartTransferDTO; 
import pe.grupoantonio.gestion.demo.exception.RecursoNoEncontradoException; 
import pe.grupoantonio.gestion.demo.repository.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoService implements ICarritoService { 
    
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private DetalleCarritoRepository detalleCarritoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;

    @Transactional
    public Carrito obtenerOCrearCarritoActivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario ID " + usuarioId + " no existe."));

        return carritoRepository.findByUsuarioAndEstado(usuario, "ACTIVO")
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito("ACTIVO", usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    @Transactional
    public DetalleCarrito agregarProducto(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");

        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado ID: " + productoId));

        // Busca si ya existe usando el método que arreglamos antes en el repositorio
        Optional<DetalleCarrito> detalleOpt = detalleCarritoRepository.findByCarritoAndProducto(carrito, producto);
        DetalleCarrito detalle;

        if (detalleOpt.isPresent()) {
            detalle = detalleOpt.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
        } else {
            detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecioVenta());
        }
        return detalleCarritoRepository.save(detalle);
    }
    
    @Transactional(readOnly = true)
    public List<DetalleCarrito> obtenerDetallesDelCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        return detalleCarritoRepository.findAll().stream()
                .filter(d -> d.getCarrito().getId().equals(carrito.getId()) && d.getCantidad() > 0)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetalleCarrito actualizarCantidad(Long usuarioId, Long detalleId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) throw new IllegalArgumentException("Cantidad inválida.");
        DetalleCarrito detalle = detalleCarritoRepository.findById(detalleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado"));
        
        if (!detalle.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acceso denegado.");
        }
        detalle.setCantidad(nuevaCantidad);
        return detalleCarritoRepository.save(detalle);
    }

    @Transactional
    public void eliminarDetalle(Long usuarioId, Long detalleId) {
        DetalleCarrito detalle = detalleCarritoRepository.findById(detalleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrado"));
        if (!detalle.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acceso denegado.");
        }
        detalleCarritoRepository.delete(detalle);
    }
    
    // ✅ MÉTODO CORREGIDO: FUSIÓN DE CARRITOS (MERGE)
    @Transactional
    public void sincronizarCarrito(Long usuarioId, List<CartTransferDTO> itemsTransferidos) { 
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);

        // No borramos nada (deleteByCarrito eliminado). Fusionamos.
        for (CartTransferDTO item : itemsTransferidos) {
            Producto producto = productoRepository.findById(Long.valueOf(item.getId()))
                                   .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getId()));
            
            // Verificamos si ya existe en la DB
            Optional<DetalleCarrito> existente = detalleCarritoRepository.findByCarritoAndProducto(carrito, producto);
            
            if (existente.isPresent()) {
                // Si existe, SUMAMOS la cantidad local a la que ya tenía en su cuenta
                DetalleCarrito det = existente.get();
                det.setCantidad(det.getCantidad() + item.getCantidad());
                detalleCarritoRepository.save(det);
            } else {
                // Si no existe, lo creamos
                DetalleCarrito detalle = new DetalleCarrito();
                detalle.setCarrito(carrito); 
                detalle.setProducto(producto); 
                detalle.setCantidad(item.getCantidad()); 
                detalle.setPrecioUnitario(item.getPrecio()); 
                detalleCarritoRepository.save(detalle);
            }
        }
    }
}