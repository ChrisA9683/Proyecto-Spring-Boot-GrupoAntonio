package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.model.Carrito;
import pe.grupoantonio.gestion.demo.model.DetalleCarrito;
import pe.grupoantonio.gestion.demo.dto.CartTransferDTO; 
import java.util.List;

public interface ICarritoService {
    Carrito obtenerOCrearCarritoActivo(Long usuarioId);
    DetalleCarrito agregarProducto(Long usuarioId, Long productoId, int cantidad);
    List<DetalleCarrito> obtenerDetallesDelCarrito(Long usuarioId);
    DetalleCarrito actualizarCantidad(Long usuarioId, Long detalleId, int nuevaCantidad);
    void eliminarDetalle(Long usuarioId, Long detalleId);
    void sincronizarCarrito(Long usuarioId, List<CartTransferDTO> itemsTransferidos);
}