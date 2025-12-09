package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metodo_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACIÓN CON USUARIO ---
    // En lugar de solo guardar el número (Long), guardamos la relación
    // para poder saber quién es el dueño del método de pago.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "tipo_pago", length = 50)
    private String tipoPago; // ej: 'TARJETA', 'YAPE', 'TRANSFERENCIA'

    @Column(name = "nombre_titular", length = 100)
    private String nombreTitular;

    @Column(name = "ultimos_digitos_tarjeta", length = 4)
    private String ultimosDigitosTarjeta;

    @Column(name = "marca_tarjeta", length = 50)
    private String marcaTarjeta;

    @Column(name = "referencia_externa", length = 100)
    private String referenciaExterna; // Para voucher o código de operación

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now(); // Asigna fecha actual automáticamente
}