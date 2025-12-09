package pe.grupoantonio.gestion.demo.dto;

import java.util.List;

public class PedidoCheckOutDTO {
  // Lista de productos del carrito
    private List<DetallePedidoDTO> detalles; 
    
    // Totales calculados en el frontend
    private Double subtotal;
    private Double igv;
    private Double total;
    
    // Nota: El usuario_id se puede manejar en el backend, 
    // pero si lo necesitas aquí, añádelo: private Integer usuario_id;

    // Generar Getters, Setters y Constructores (si no usas Lombok)
    
    public List<DetallePedidoDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoDTO> detalles) { this.detalles = detalles; }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public Double getIgv() { return igv; }
    public void setIgv(Double igv) { this.igv = igv; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}