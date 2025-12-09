package pe.grupoantonio.gestion.demo.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculoService {
    
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final BigDecimal SHIPPING_SHALOM = new BigDecimal("70.00");
    private static final BigDecimal SHIPPING_PROPIO = new BigDecimal("50.00");

    public BigDecimal calcularCostoEnvio(String metodoEnvio) {
        
        if ("RECOJO_TIENDA".equalsIgnoreCase(metodoEnvio)) {
            return BigDecimal.ZERO;
        } else if ("SHALOM".equalsIgnoreCase(metodoEnvio)) {
            return SHIPPING_SHALOM;
        } else { 
            return SHIPPING_PROPIO;
        }
    }
    
    public BigDecimal recalcularTotal(BigDecimal subtotal, BigDecimal costoEnvio) {
        
        BigDecimal igv = subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal total = subtotal
            .add(igv)
            .add(costoEnvio)
            .setScale(2, RoundingMode.HALF_UP);
            
        return total;
    }
}