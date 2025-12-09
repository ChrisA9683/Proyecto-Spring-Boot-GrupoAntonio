package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cotizacion")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Inicializamos la fecha automáticamente al crear el objeto
    private LocalDateTime fecha = LocalDateTime.now();

    private Double total;

    private String estado;

    // --- RELACIÓN CON USUARIO ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    // --- RELACIÓN CON DETALLES ---
    // CascadeType.ALL permite que si guardas la cotización, se guarden sus detalles automáticamente
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DetalleCotizacion> detalles = new ArrayList<>();
}