package pe.grupoantonio.gestion.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.grupoantonio.gestion.demo.dto.CartTransferDTO;

// Esta clase POJO se mantiene en la sesión de Spring (@SessionAttributes)
@Data // Genera Getters, Setters, toString, equals, hashcode
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutData implements Serializable {
    
    private static final long serialVersionUID = 1L; // Buena práctica para clases Serializable

    // --- DATOS DE CONTACTO Y DIRECCIÓN (Paso 1) ---
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private String referencia;
    private String departamento;
    private String distrito;
    
    // --- OPCIONES DE ENVÍO Y FACTURACIÓN ---
    private String metodoEnvio;
    
    private BigDecimal costoEnvio;  
    
    private boolean solicitaFactura;
    private String ruc;
    private String razonSocial;
    
    // --- DATOS DE PAGO Y TOTALES (Paso 2) ---
    private String metodoPago;
    private BigDecimal subtotal;
    private BigDecimal total;
    
    // --- CAMPOS ESPECÍFICOS DEL FORMULARIO DE PAGO ---
    private String nombreTarjeta;      // Titular de la tarjeta
    private String referenciaExterna;  // Código de operación (Yape/Plin/Transferencia)
    private String numTarjeta;         // Número de la tarjeta
    
    // Lista de productos que se están comprando
    private List<CartTransferDTO> itemsCarrito;
}