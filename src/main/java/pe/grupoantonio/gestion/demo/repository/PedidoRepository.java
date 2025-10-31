package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.grupoantonio.gestion.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
