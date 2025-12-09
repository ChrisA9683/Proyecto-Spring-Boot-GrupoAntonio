package pe.grupoantonio.gestion.demo.controller;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

import pe.grupoantonio.gestion.demo.model.CheckoutData;
import pe.grupoantonio.gestion.demo.dto.CartTransferDTO;
import pe.grupoantonio.gestion.demo.service.PedidoService;
import pe.grupoantonio.gestion.demo.service.CalculoService;
import pe.grupoantonio.gestion.demo.service.IUsuarioService;
import pe.grupoantonio.gestion.demo.service.ICarritoService;
import pe.grupoantonio.gestion.demo.model.Usuario;

import java.util.List;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.HttpStatus;

@Controller
@SessionAttributes("checkoutData")
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CalculoService calculoService;

    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private ICarritoService carritoService; 

    @ModelAttribute("checkoutData")
    public CheckoutData setUpCheckoutData() {
        return new CheckoutData();
    }

    // ====================================================================
    // 1. API: SINCRONIZACIÓN DESDE JAVASCRIPT (AJAX)
    // ====================================================================
    @PostMapping("/iniciar-transferencia")
    @ResponseBody // Retorna JSON porque lo llama carrito.js
    public ResponseEntity<?> iniciarTransferencia(
            @RequestBody List<CartTransferDTO> cartItems, 
            @ModelAttribute("checkoutData") CheckoutData checkoutData,
            Principal principal) {
        
        if (cartItems == null || cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Carrito vacío"));
        }

        // A. Sincronización DB
        if (principal != null) {
            Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
            if (usuario != null && usuario.getId() != null) {
                try {
                    carritoService.sincronizarCarrito(usuario.getId(), cartItems);
                } catch (Exception e) {
                    e.printStackTrace(); 
                }
            }
        }

        // B. Cálculo de Sesión (CheckoutData)
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartTransferDTO item : cartItems) {
            BigDecimal itemPrice = item.getPrecio() != null ? BigDecimal.valueOf(item.getPrecio()) : BigDecimal.ZERO;
            BigDecimal itemQty = BigDecimal.valueOf(item.getCantidad());
            subtotal = subtotal.add(itemPrice.multiply(itemQty));
        }

        checkoutData.setItemsCarrito(cartItems);
        checkoutData.setSubtotal(subtotal);
        checkoutData.setCostoEnvio(BigDecimal.ZERO);
        checkoutData.setTotal(subtotal); // Envío se suma luego
        
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ====================================================================
    // 2. VISTAS: FLUJO DE CHECKOUT (HTML)
    // ====================================================================

    @GetMapping("/datos")
    public String showDatosForm(@ModelAttribute("checkoutData") CheckoutData checkoutData, Principal principal) {
        if (principal == null) return "redirect:/usuario/login?redirect=/checkout/datos";

        if (checkoutData.getSubtotal() == null || checkoutData.getSubtotal().compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/usuario/carrito"; 
        }
        return "checkout-paso1-datos"; 
    }

    @PostMapping("/datos")
    public String processDatosForm(@ModelAttribute("checkoutData") CheckoutData checkoutData, RedirectAttributes redirectAttributes) {
        if (checkoutData.getNombre() == null || checkoutData.getDireccion() == null || checkoutData.getMetodoEnvio() == null) {
            redirectAttributes.addFlashAttribute("error", "Por favor complete todos los datos de envío.");
            return "redirect:/checkout/datos";
        }
        
        BigDecimal nuevoCostoEnvio = calculoService.calcularCostoEnvio(checkoutData.getMetodoEnvio());
        BigDecimal nuevoTotal = checkoutData.getSubtotal().add(nuevoCostoEnvio);
        
        checkoutData.setCostoEnvio(nuevoCostoEnvio);
        checkoutData.setTotal(nuevoTotal); 
        
        return "redirect:/checkout/pago"; 
    }

    @GetMapping("/pago")
    public String showPagoForm(@ModelAttribute("checkoutData") CheckoutData checkoutData, Principal principal) {
        if (principal == null) return "redirect:/usuario/login?redirect=/checkout/pago";

        if (checkoutData.getDireccion() == null) {
            return "redirect:/checkout/datos";
        }
        return "checkout-paso2-pago"; 
    }

    // ====================================================================
    // 3. PROCESAR ORDEN FINAL (SUBMIT FORMULARIO HTML)
    // ====================================================================
    @PostMapping("/confirmar-orden")
    // ❌ ELIMINADO @ResponseBody: Ahora retorna String para redirección
    public String confirmarOrden(
            @ModelAttribute("checkoutData") CheckoutData checkoutData, 
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam(value = "nombreTarjeta", required = false) String nombreTarjeta,
            @RequestParam(value = "referenciaExterna", required = false) String referenciaExterna,
            Principal principal,
            SessionStatus status,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 1. Validar autenticación
            if (principal == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión expirada. Inicie sesión de nuevo.");
                return "redirect:/usuario/login";
            }

            // 2. Actualizar datos de pago en el objeto de sesión
            checkoutData.setMetodoPago(metodoPago);
            checkoutData.setNombreTarjeta(nombreTarjeta);
            checkoutData.setReferenciaExterna(referenciaExterna);

            // 3. Validaciones básicas
            if ("TARJETA".equals(metodoPago) && (nombreTarjeta == null || nombreTarjeta.trim().isEmpty())) {
                throw new ValidationException("Debe ingresar el nombre del titular de la tarjeta.");
            }

            // 4. Obtener ID de usuario
            Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
            if (usuario == null) throw new RuntimeException("Usuario no encontrado.");

            // 5. Procesar pedido
            Long pedidoId = pedidoService.crearYProcesar(usuario.getId(), checkoutData);

            // 6. Limpiar sesión y redirigir a ÉXITO
            status.setComplete();
            return "redirect:/checkout/exito?pedidoId=" + pedidoId;

        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout/pago"; // Volver al formulario de pago con error
        } catch (Exception e) {
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("error", "Error interno al procesar el pedido: " + e.getMessage());
            return "redirect:/checkout/pago";
        }
    }
    
    @GetMapping("/exito")
    public String showSuccessPage(@RequestParam Long pedidoId, Model model) {
        model.addAttribute("pedidoId", pedidoId);
        return "checkout-exito"; 
    }
}