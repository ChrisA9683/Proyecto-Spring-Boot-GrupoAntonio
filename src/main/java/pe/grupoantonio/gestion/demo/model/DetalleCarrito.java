
package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DetalleCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
     // 🔗 Relación con Carrito
    // Un carrito puede tener muchos detalles (productos dentro del carrito)
    @ManyToOne
    @JoinColumn (name="carrito_id", nullable = false)
    private Carrito carrito;
    
      // 🔗 Relación con Producto
    // Cada detalle hace referencia a un producto específico
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    // 📌 Cantidad de unidades del producto en el carrito
    @Column(nullable= false)
    private int cantidad;
    
    // 📌 Precio unitario del producto al momento de agregarlo al carrito
    @Column(nullable=false)
    private double precioUnitario;
    
 // 📌 Subtotal = cantidad * precioUnitario
    // No se almacena directamente en BD (es calculado en memoria)
    @Transient
    public double getSubtotal(){
    return cantidad * precioUnitario;
}
    
    
    
}
    