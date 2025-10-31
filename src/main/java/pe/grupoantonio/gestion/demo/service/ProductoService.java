
package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.dto.ProductoDTO;
import java.util.List;
public interface ProductoService {
    ProductoDTO crearProducto(ProductoDTO productoDTO);
    ProductoDTO obtenerProductoPorId(Long id);
    List<ProductoDTO> listarProducto();
    ProductoDTO actualizaProducto(Long id, ProductoDTO productoDTO);
    void eliminarProducto(Long id);
    
    
}
