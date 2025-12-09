package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    private String descripcion;
    
    private Double precio;

    @Column(name = "imagen_url") // Mapeo explícito para coincidir con la base de datos
    private String imagenUrl;

    private String unidad;

    private Integer cantidad; 

    private String categoria; 

    private String estado; 
    
      public Double getPrecioVenta() {
        // En una aplicación real, aquí aplicarías descuentos o lógica de precios.
        return this.precio;
    }
}