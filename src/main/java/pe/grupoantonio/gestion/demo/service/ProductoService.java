
package pe.grupoantonio.gestion.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import pe.grupoantonio.gestion.demo.model.Producto;
import pe.grupoantonio.gestion.demo.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Método para obtener todos los productos
    public List<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }
}
