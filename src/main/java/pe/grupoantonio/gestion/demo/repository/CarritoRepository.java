
package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Carrito;

// Interfaz que extiende JpaRepository para CRUD automático
// JpaRepository<Entidad, TipoID>
public interface CarritoRepository extends JpaRepository<Carrito, Long>{ 
    
}
