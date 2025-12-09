package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data // Esto incluye @Getter y @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // Ejemplo: "ADMIN", "USER"

    // --- ¡MÉTODOS MANUALES ELIMINADOS! ---
}
