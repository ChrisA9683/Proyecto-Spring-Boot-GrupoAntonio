package pe.grupoantonio.gestion.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Habilita @PreAuthorize en Controladores
public class SecurityConfig {

    @Autowired
    private CustomAuthSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(auth -> auth
                // 1. RUTAS PÚBLICAS (Sin autenticación requerida)
                .requestMatchers(HttpMethod.POST,
                    "/checkout/iniciar-transferencia",
                    "/checkout/datos",
                    "/checkout/confirmar-orden",
                    "/usuario/login",
                    "/usuario/registro").permitAll()
                
                // Rutas que no son POST pero deben ser públicas
                .requestMatchers("/checkout/**", "/usuario/login", "/usuario/registro").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/imagenes/**", "/api/**").permitAll()
                .requestMatchers("/", "/home", "/index").permitAll()
                .requestMatchers("/usuario/catalogo", "/usuario/servicios", "/usuario/contacto", "/usuario/carrito").permitAll()
                
                // =======================================================
                // 🔒 2. REGLAS DE ROLES (AJUSTADAS A USUARIOController)
                // =======================================================
                
                // A. VENDEDOR y ADMIN: Ver y Gestionar Pedidos
                .requestMatchers("/usuario/pedidos").hasAnyRole("ADMIN", "VENDEDOR")

                // B. SOLO ADMIN: Gestión de Datos Sensibles y Estadísticas
                .requestMatchers("/usuario/dashboard", "/usuario/panel_productos", "/usuario/panel_usuario").hasRole("ADMIN")

                // C. CLIENTE y ADMIN: Ver Seguimiento de Pedidos
                // Nota: Usamos 'USER' para el rol de cliente, ajustando el alcance.
                .requestMatchers("/usuario/seguimiento").hasAnyRole("USER", "ADMIN")
                
                // 3. Cualquier otra ruta requiere autenticación.
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/usuario/login")
                .loginProcessingUrl("/usuario/login")
                .successHandler(successHandler) // Tu handler de redirección por roles
                .permitAll()
            )
            // --- FIX: SOLUCIÓN AL ERROR 405 (Conflictos de URL) ---
            .logout(logout -> logout
                .logoutUrl("/ejecutar-logout") // URL única fuera del namespace /usuario/
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
    
    // --- BEANS ESENCIALES ---

    @Bean
    public PasswordEncoder passwordEncoder() { 
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}