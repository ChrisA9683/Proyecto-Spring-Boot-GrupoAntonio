
package pe.grupoantonio.gestion.demo.repository;

import java.util.List;
import pe.grupoantonio.gestion.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ProductoRepository extends JpaRepository<Producto, Long>{
    
    //Buscar el Producto que contengan la plabara nombre//
    List<Producto> findByNombre(String nombre);
    
    //Buscar producto que contengan una palabra en el nombre
    List<Producto> findByNombreContaining (String palabra);
    
    //Buscar Producto  con precios mayor a cierto valor//
    List<Producto> findByPrecioGreaterThan(Double precio);
    
    //Buscar producto entre precios//
    List<Producto> findByPrecioBetween(double min, double max);
    
    
}
