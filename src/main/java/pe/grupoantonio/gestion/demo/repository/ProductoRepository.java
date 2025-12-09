package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // No es necesario definir un método personalizado si deseas obtener todos los productos
}
