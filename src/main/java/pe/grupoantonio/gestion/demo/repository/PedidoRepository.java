package pe.grupoantonio.gestion.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.grupoantonio.gestion.demo.model.Pedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional; // <-- Necesario para obtener el Optional de Pedido

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    long countByEstado(String estado);

    // --- NUEVO MÉTODO PARA PAGINACIÓN ---
    Page<Pedido> findAllByOrderByFechaCreacionDesc(Pageable pageable);

    // Consulta de ganancias (Suma VENDIDO y PAGADO)
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado IN ('PAGADO', 'VENDIDO')")
    BigDecimal sumarGananciasTotales();
    
    // (Opcional) Mantenido
    List<Pedido> findTop10ByOrderByFechaCreacionDesc();

    // =========================================================================
    // ✨ MÉTODO CLAVE AÑADIDO: Obtener el Pedido más reciente por ID de Usuario
    // =========================================================================
    
    /**
     * Busca el pedido más reciente (Top 1) para un cliente (Usuario)
     * ordenado por FechaCreacion descendente.
     * Retorna un Optional para manejar el caso de que el cliente no tenga pedidos.
     */
    Optional<Pedido> findTopByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}