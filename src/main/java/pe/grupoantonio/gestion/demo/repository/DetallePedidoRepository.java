package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}
