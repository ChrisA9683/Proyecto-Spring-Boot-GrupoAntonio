package pe.grupoantonio.gestion.demo.dto;

public class DetallePedidoDTO {
    
    // El ID del producto que viene del frontend (data-id)
    private Long producto_id; 
    
    // La cantidad seleccionada
    private Integer cantidad; 
    
    // El precio unitario enviado desde el frontend
    private Double precio_unitario;

    // --- Getters y Setters ---

    public Long getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(Long producto_id) {
        this.producto_id = producto_id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(Double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
}