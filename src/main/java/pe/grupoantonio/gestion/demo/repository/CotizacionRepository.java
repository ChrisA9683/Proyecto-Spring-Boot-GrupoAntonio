package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Cotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
}
