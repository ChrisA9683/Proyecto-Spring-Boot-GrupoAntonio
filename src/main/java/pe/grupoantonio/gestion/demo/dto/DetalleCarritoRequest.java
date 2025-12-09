
package pe.grupoantonio.gestion.demo.dto;

import pe.grupoantonio.gestion.demo.model.Producto;

public class DetalleCarritoRequest {
    private int cantidad;
    private double precioUnitario;
    private Producto producto;

    // Getters y setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
