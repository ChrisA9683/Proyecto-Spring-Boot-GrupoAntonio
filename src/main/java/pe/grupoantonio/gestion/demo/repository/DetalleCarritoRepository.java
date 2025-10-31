
package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.DetalleCarrito;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito,Long> {
   
}
