package pe.grupoantonio.gestion.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.grupoantonio.gestion.demo.model.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Buscar todos los detalles de un pedido
    List<DetallePedido> findByPedidoId(Long pedidoId);
}