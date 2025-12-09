package pe.grupoantonio.gestion.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import pe.grupoantonio.gestion.demo.service.AIService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired private AIService aiService;

    // El endpoint del frontend (JS) llama a /api/chat/responder, no /api/chat
    @PostMapping("/responder") 
    public Map<String, String> chatear(@RequestBody Map<String, String> payload) {
        
        // CORRECCIÓN CLAVE: Leer la clave 'mensajeUsuario' que es la que envía el frontend
        String mensajeUsuario = payload.get("mensajeUsuario");

        if (mensajeUsuario == null || mensajeUsuario.isEmpty()) {
            return Map.of("respuesta", "Por favor, escribe un mensaje válido. 🤔");
        }

        String respuestaIA = aiService.obtenerRespuestaIA(mensajeUsuario);

        // El frontend espera la clave 'respuesta', no 'response'
        return Map.of("respuesta", respuestaIA);
    }
}