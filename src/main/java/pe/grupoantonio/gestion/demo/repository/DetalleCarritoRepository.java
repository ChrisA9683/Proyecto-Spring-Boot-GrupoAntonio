package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.DetalleCarrito;
import pe.grupoantonio.gestion.demo.model.Carrito;
import pe.grupoantonio.gestion.demo.model.Producto;

import java.util.Optional;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {

    // ✅ ESTE ES EL MÉTODO QUE FALTABA Y CAUSABA EL ERROR DE COMPILACIÓN
    // Spring Data JPA generará automáticamente la consulta basada en los objetos.
    Optional<DetalleCarrito> findByCarritoAndProducto(Carrito carrito, Producto producto);

    // Método para limpiar el carrito
    void deleteByCarrito(Carrito carrito);
}