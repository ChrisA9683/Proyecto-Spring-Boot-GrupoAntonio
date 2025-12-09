package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Carrito;
import pe.grupoantonio.gestion.demo.model.Usuario;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    // MÉTODO REQUERIDO por CarritoService.obtenerOCrearCarritoActivo
    Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, String estado);

    // Método usado en el código anterior (sin estado)
    Optional<Carrito> findByUsuario(Usuario usuario);
}