
package pe.grupoantonio.gestion.demo.dto;

import java.util.List;
import pe.grupoantonio.gestion.demo.model.Usuario;

public class CheckoutRequest {

    private Usuario usuario;
    private List<DetalleCarritoRequest> detalles;

    // Getters y setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleCarritoRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCarritoRequest> detalles) {
        this.detalles = detalles;
    }
}
