// 📄 CheckoutResponse.java (Corregido con todos los Getters/Setters esperados)

package pe.grupoantonio.gestion.demo.dto;


public class CheckoutResponse {
    private Long carritoId;
    private String redirectUrl;

    public CheckoutResponse(Long carritoId, String redirectUrl) {
        this.carritoId = carritoId;
        this.redirectUrl = redirectUrl;
    }

    // Getters y setters
    public Long getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
