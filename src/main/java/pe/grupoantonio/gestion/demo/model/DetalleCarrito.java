package pe.grupoantonio.gestion.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importante
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "detalle_carrito")
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    @Column(name = "precio_unitario") 
    private Double precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @ToString.Exclude 
    @JsonIgnore // ✅ ESTO ES LO QUE ARREGLA EL ERROR 500 DE "NESTING DEPTH"
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para que traiga los datos del producto siempre
    @JoinColumn(name = "producto_id", nullable = false)
    @ToString.Exclude
    private Producto producto;
}