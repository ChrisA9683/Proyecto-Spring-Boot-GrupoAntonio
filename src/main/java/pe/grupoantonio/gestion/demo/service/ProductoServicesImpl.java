package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.repository.ProductoRepository;
import pe.grupoantonio.gestion.demo.dto.ProductoDTO;
import pe.grupoantonio.gestion.demo.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServicesImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());

        Producto guardado = productoRepository.save(producto);

        ProductoDTO dto = new ProductoDTO();
        dto.setId(guardado.getId()); // ✅ no olvides setear id también
        dto.setNombre(guardado.getNombre());
        dto.setDescripcion(guardado.getDescripcion());
        dto.setPrecio(guardado.getPrecio());

        return dto;
    }

    @Override
    public ProductoDTO obtenerProductoPorId(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProductoDTO> listarProducto() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ProductoDTO actualizaProducto(Long id, ProductoDTO productoDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void eliminarProducto(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}