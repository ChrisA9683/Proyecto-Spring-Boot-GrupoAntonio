package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.MetodoPago; // Asegúrate de importar tu entidad

// La interfaz debe extender JpaRepository, usando tu entidad MetodoPago y el tipo de su clave primaria (Long)
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    public MetodoPago save(MetodoPago metodoPago);
    
}