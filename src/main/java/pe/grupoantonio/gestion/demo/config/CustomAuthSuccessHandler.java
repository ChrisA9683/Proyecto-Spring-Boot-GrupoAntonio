package pe.grupoantonio.gestion.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Authentication authentication
    ) throws IOException, ServletException {
        
        // 🔍 DEBUG
        System.out.println("\n========= LOGIN EXITOSO =========");
        System.out.println("👤 Usuario: " + authentication.getName());

        // -----------------------------------------------------------
        // 1. PRIORIDAD: REDIRECCIÓN FORZADA (CARRITO)
        // -----------------------------------------------------------
        String redirectUrl = request.getParameter("redirect");
        
        if (redirectUrl != null && !redirectUrl.isEmpty() && !redirectUrl.equals("null")) {
            System.out.println("✅ Redirigiendo a destino forzado: " + redirectUrl);
            response.sendRedirect(redirectUrl);
            return; // 🛑 IMPORTANTE: Detener aquí si hay redirección
        }

        // -----------------------------------------------------------
        // 2. SIN REDIRECCIÓN: DISTRIBUCIÓN POR ROLES
        // -----------------------------------------------------------
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean esAdmin = false;
        boolean esVendedor = false;
        
        // Detectar roles
        for (GrantedAuthority authority : authorities) {
            String rol = authority.getAuthority();
            if (rol.equals("ROLE_ADMIN") || rol.equals("ADMIN")) {
                esAdmin = true;
            }
            if (rol.equals("ROLE_VENDEDOR") || rol.equals("VENDEDOR")) {
                esVendedor = true;
            }
        }

        System.out.println("🛠️ Roles detectados -> Admin: " + esAdmin + " | Vendedor: " + esVendedor);

        if (esAdmin) {
            // ADMIN -> Va al Dashboard General (Tiene permiso total)
            System.out.println("➡ Redirigiendo a Dashboard (Admin)");
            response.sendRedirect("/usuario/dashboard");
        } 
        else if (esVendedor) {
            // VENDEDOR -> Va a Pedidos (NO tiene permiso a Dashboard General)
            System.out.println("➡ Redirigiendo a Pedidos (Vendedor)");
            response.sendRedirect("/usuario/pedidos");
        } 
        else {
            // CLIENTE -> Va a Seguimiento (NO tiene permiso a Dashboard ni Pedidos globales)
            System.out.println("➡ Redirigiendo a Seguimiento (Cliente)");
            response.sendRedirect("/usuario/seguimiento"); 
        }
        System.out.println("=================================\n");
    }
}