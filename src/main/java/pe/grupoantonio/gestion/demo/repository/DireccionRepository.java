package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Direccion;


public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    // Métodos personalizados aquí, si son necesarios
}