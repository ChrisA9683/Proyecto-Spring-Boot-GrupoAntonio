package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "tipo_direccion")
    private String tipoDireccion;

    @Column(name = "nombre_receptor")
    private String nombreReceptor;

    @Column(name = "telefono_receptor")
    private String telefonoReceptor;

    private String calle;

    private String referencia;

    private String distrito;

    private String departamento;

    private String ciudad;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
