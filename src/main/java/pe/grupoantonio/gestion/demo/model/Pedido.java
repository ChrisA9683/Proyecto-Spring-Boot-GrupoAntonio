package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACIONES ---
    // Estas son las que tu Service necesita para vincular el pedido al usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude // Importante: Evita errores de memoria al imprimir logs
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id")
    @ToString.Exclude
    private MetodoPago metodoPago;
    
    // Si en tu base de datos direccion_id es solo un número y no una tabla aparte, esto está bien:
    @Column(name = "direccion_id")
    private Long direccionId;

    // --- DATOS ECONÓMICOS ---
    
    private BigDecimal total;

    @Column(name = "costo_envio")
    private BigDecimal costoEnvio;

    // --- ESTADO Y FECHA ---

    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // --- DETALLES DEL PEDIDO ---
    // Relación inversa para guardar los productos de este pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DetallePedido> detalles = new ArrayList<>();
}