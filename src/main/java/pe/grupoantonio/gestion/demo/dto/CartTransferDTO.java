package pe.grupoantonio.gestion.demo.dto;

// Clase para mapear cada producto que viene del JavaScript
public class CartTransferDTO {
    
    private String id; // ID del producto
    private String nombre;
    private Integer cantidad;
    private Double precio; // Usamos Double para recibir del JS/JSON
    // ... si tu JS guarda más campos como imagen, unidad, añádelos aquí.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    
}