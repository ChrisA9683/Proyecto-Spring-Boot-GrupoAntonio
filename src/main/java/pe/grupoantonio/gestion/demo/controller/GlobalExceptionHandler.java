package pe.grupoantonio.gestion.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ MODIFICADO: Usamos 'Throwable' para capturar ABSOLUTAMENTE TODO.
    // Esto incluye errores graves que no son 'Exceptions' normales.
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleGlobalException(Throwable ex, HttpServletRequest request) {
        
        // 1. IMPRESIÓN FORZADA EN CONSOLA (System.err imprime en rojo en la mayoría de IDEs)
        System.err.println("\n\n==================== 🔥 ERROR CRÍTICO DETECTADO (NIVEL THROWABLE) 🔥 ====================");
        System.err.println("📌 RUTA SOLICITADA: " + request.getRequestURI());
        System.err.println("❌ TIPO DE ERROR: " + ex.getClass().getName());
        System.err.println("💬 MENSAJE: " + ex.getMessage());
        System.err.println("------------------------- STACK TRACE INICIO -------------------------");
        ex.printStackTrace(); // Imprime la traza completa del error
        System.err.println("------------------------- STACK TRACE FIN ----------------------------\n\n");

        // 2. Respuesta al Frontend (para que veas el error técnico en la alerta del navegador)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "error", "DEBUG: " + ex.getMessage(),
                    "exception", ex.getClass().getSimpleName()
                ));
    }
}