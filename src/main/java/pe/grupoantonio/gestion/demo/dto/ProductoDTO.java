package pe.grupoantonio.gestion.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.sql.Update;

public class ProductoDTO {
    
    @NotNull(message = "El id no puede ser nulo", groups = Update.class) // opcional, solo si usas validaciones
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor o igual a 1")
    private Double precio;

    @NotBlank (message = "Agregue una descripcion profavor")
    private String Descripcion;
    

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    
    public String getDescripcion(){
     return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
    
    public Long getId() {
    return id;
    }

    public void setId(Long id) {
    this.id = id;}
}

