package pe.grupoantonio.gestion.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    @GetMapping("/usuario/confirmacion")
    public String mostrarConfirmacion(
        @RequestParam(name = "id", required = false) Long carritoId,
        Model model) {

        model.addAttribute("carritoId", carritoId != null ? carritoId : "No disponible");
        
        // Retorna la plantilla HTML: src/main/resources/templates/confirmacion_pedido.html
        return "confirmacion_pedido"; 
    }
    
    
}