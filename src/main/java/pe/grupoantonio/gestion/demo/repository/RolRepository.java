package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Rol;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método para buscar roles por su nombre (ej: "USER", "ADMIN")
    Optional<Rol> findByNombre(String nombre);
}